// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.completion

import com.google.idea.gn.GnCompletionContributor
import com.google.idea.gn.GnKeys
import com.google.idea.gn.GnLabel
import com.google.idea.gn.config.gnRoot
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.Target
import com.google.idea.gn.util.getPathLabel
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.util.ProcessingContext
import com.intellij.util.text.Matcher
import java.util.*

class FileCompletionProvider @JvmOverloads constructor(private val mFileMatcher: Matcher, private val skipFileName: Boolean = false, private val parseTargets: Boolean = false) : CompletionProvider<CompletionParameters>() {
  override fun addCompletions(parameters: CompletionParameters,
                              context: ProcessingContext, _result: CompletionResultSet) {
    var result = _result
    var text = parameters.position.text
    if (text.startsWith("\"")) {
      text = text.substring(1)
    }
    if (text.endsWith("\"")) {
      text = text.substring(0, text.length - 1)
    }
    text = text.replace(
        GnCompletionContributor.DUMMY_ID, "", false)
    result = result.withPrefixMatcher(
        CamelHumpMatcher(text, true))
    val path: GnLabel? = GnLabel.parse(text)
    var dir: VirtualFile? = null
    val base: VirtualFile?
    val absolute: Boolean
    val project = parameters.editor.project ?: return
    if (path != null) {
      absolute = path.isAbsolute
      base = if (absolute) {
        project.gnRoot
      } else {
        parameters.originalFile.containingDirectory.virtualFile
      }
      if (base != null) {
        var parts = path.parts
        if (!text.endsWith("/") && parts.isNotEmpty()) {
          parts = parts.copyOfRange(0, parts.size - 1)
        }
        dir = VfsUtil.findRelativeFile(base, *parts)
      }
    } else {
      if (text.startsWith("//")) {
        dir = project.gnRoot
        base = dir
        absolute = true
      } else {
        dir = parameters.originalFile.containingDirectory.virtualFile
        base = dir
        absolute = false
      }
    }
    if (dir != null) {
      completeFrom(dir, base!!, result, absolute,
          PsiManager.getInstance(project))
    }
  }

  private fun completeFrom(directory: VirtualFile, base: VirtualFile,
                           result: CompletionResultSet, absolute: Boolean, manager: PsiManager) {
    VfsUtilCore.visitChildrenRecursively(directory,
        object : VirtualFileVisitor<Void?>(
            limit(5)) {

          private fun getPath(file: VirtualFile): String = getPathLabel(file, base, absolute) ?: ""

          override fun afterChildrenVisited(file: VirtualFile) {
            super.afterChildrenVisited(file)
            if (!file.isDirectory || stack.empty()) {
              return
            }
            val state = stack.pop()
            if (state.foundBuildFile) {
              if (!state.foundNamedTarget) {
                val path = getPath(file)
                if (path.isNotEmpty() && path != "//") {
                  result.addElement(
                      createElementBuilderWithIcon(path,
                          CompleteType.DIRECTORY))
                }
              }
              if (!stack.empty()) {
                stack.peek().foundBuildFile = true
              }
            }
          }

          private fun createElementBuilderWithIcon(lookup: String, type: CompleteType, file: PsiFile? = null): LookupElementBuilder {
            val elementBuilder = LookupElementBuilder.create(
                lookup)
            elementBuilder.putUserData(GnKeys.LABEL_COMPLETION_TYPE, type)
            val icon = when (type) {
              CompleteType.TARGET -> AllIcons.Nodes.Target
              CompleteType.DIRECTORY -> AllIcons.Nodes.Folder
              CompleteType.FILE -> file?.getIcon(
                  0)
            } ?: return elementBuilder
            return elementBuilder.withIcon(icon)
          }

          override fun visitFile(_file: VirtualFile): Boolean {
            var file = _file
            ProgressManager.checkCanceled()
            val name = file.name
            if (file.isDirectory) {
              stack.push(VisitingState())
              return true
            }
            if (!mFileMatcher.matches(name)) {
              return false
            }
            if (stack.empty()) {
              return false
            }
            stack.peek().foundBuildFile = true
            var targets: Map<String, Target>? = null
            if (parseTargets) {
              val psi = manager.findFile(file) as? GnFile
                  ?: return false
              val scope = psi.scope
              ProgressManager.checkCanceled()
              targets = scope.targets
              if (targets == null) {
                return false
              }
            }
            if (skipFileName) {
              file = file.parent
            }
            val basePath = getPath(file)
            if (!targets.isNullOrEmpty()) {
              val baseName = file.name
              for (target in targets.keys) {
                var suggestion: String
                if (target == baseName) {
                  suggestion = basePath
                  stack.peek().foundNamedTarget = true
                } else {
                  suggestion = "$basePath:$target"
                }
                if (suggestion.isNotEmpty()) {
                  result
                      .addElement(createElementBuilderWithIcon(suggestion,
                          CompleteType.TARGET))
                }
              }
            } else {
              result.addElement(createElementBuilderWithIcon(basePath,
                  CompleteType.FILE,
                  manager.findFile(file)))
            }
            return false
          }

          private val stack = Stack<VisitingState>()
        }
    )
  }

  private class VisitingState {
    var foundBuildFile = false
    var foundNamedTarget = false
  }

  enum class CompleteType { TARGET, DIRECTORY, FILE }

}
