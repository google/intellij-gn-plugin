//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightPlatformCodeInsightTestCase
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl

class AutoCompleteTest : LightPlatformCodeInsightTestCase() {
  override fun getTestDataPath() = "src/test/testData/project/"

  private fun copyTestFiles(filter: (VirtualFile) -> Boolean) {
    runWriteAction {
      VfsUtil.copyDirectory(this, getVirtualFile(""), project.guessProjectDir()!!, filter)
    }
  }

  private fun copyTestFilesByPath(filter: (String) -> Boolean) {
    copyTestFiles { filter(VfsUtil.getRelativePath(it, getVirtualFile(""))!!) }
  }

  fun testLabelCompletion() {
    copyTestFilesByPath {
      when (it) {
        "BUILD.gn",
        "src",
        "src/BUILD.gn",
        "src/lib",
        "src/lib/BUILD.gn",
        "src/test",
        "src/test/BUILD.gn" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """group("g"){deps=["<caret>"]}""")
    CodeInsightTestFixtureImpl.instantiateAndRun(file, editor, IntArray(0), false)
    assertEquals("[:g@TARGET, src@DIRECTORY, src/lib@DIRECTORY, src/lib:my_lib@TARGET, src/test@TARGET, src:my_src@TARGET]",
        performCompletion().joinToString(", ", "[", "]") { "${it.lookupString}@${it.getUserData(GnKeys.LOOKUP_ITEM_TYPE)}" })
  }

  private fun performCompletion(): List<LookupElement> {
    CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor)
    return LookupManager.getActiveLookup(editor)?.items ?: emptyList()
  }
}