// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.completion

import com.google.idea.gn.GnKeys
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.builtin.Import
import com.google.idea.gn.psi.builtin.Template
import com.google.idea.gn.psi.scope.FileScope
import com.google.idea.gn.psi.scope.Scope
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfTypes
import com.intellij.psi.util.parents
import com.intellij.util.ProcessingContext

class IdentifierCompletionProvider : CompletionProvider<CompletionParameters>() {
  override fun addCompletions(parameters: CompletionParameters, context: ProcessingContext, _result: CompletionResultSet) {
    val result = IdentifierCompletionResultSet(_result)

    val position = parameters.position
    val file = position.containingFile
    if (file !is GnFile) {
      return
    }

    val stopAt = position.parentOfTypes(GnStatement::class,
        GnBlock::class, GnFile::class) ?: file


    val capturingVisitor = object : Visitor.VisitorDelegate() {
      var finalScope: Scope? = null

      override fun resolveCall(call: GnCall, function: Function?): Visitor.CallAction =
          when {
            // Template and import calls must be executed so they show up in the scope.
            function is Template || function is Import -> Visitor.CallAction.EXECUTE
            position.parents(true).contains(call) -> Visitor.CallAction.VISIT_BLOCK
            else -> Visitor.CallAction.SKIP
          }


      override fun afterVisit(element: PsiElement, scope: Scope): Boolean {
        if (element == stopAt) {
          finalScope = scope
          return true
        }
        return false
      }
    }
    file.accept(Visitor(FileScope(), capturingVisitor))

    val scope = capturingVisitor.finalScope ?: file.scope

    val inFunction = position.parentOfTypes(GnCall::class)
        ?.getUserData(GnKeys.CALL_RESOLVED_FUNCTION)


    scope.gatherCompletionIdentifiers {
      ProgressManager.checkCanceled()

      if (it == inFunction) {
        it.gatherChildren { child ->
          result.addIdentifier(child)
        }
      }

      // Don't suggest target functions within a target function.
      if (inFunction?.identifierType != CompletionIdentifier.IdentifierType.TARGET_FUNCTION
          || it.identifierType != CompletionIdentifier.IdentifierType.TARGET_FUNCTION) {
        result.addIdentifier(it)
      }
    }
  }
}
