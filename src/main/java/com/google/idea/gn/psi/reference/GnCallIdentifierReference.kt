// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.psi.reference

import com.google.idea.gn.GnKeys
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.TemplateFunction
import com.google.idea.gn.psi.Visitor
import com.google.idea.gn.psi.builtin.Import
import com.google.idea.gn.psi.builtin.Template
import com.google.idea.gn.psi.scope.FileScope
import com.google.idea.gn.psi.scope.Scope
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.parents

class GnCallIdentifierReference(val call: GnCall) : PsiReferenceBase<PsiElement?>(
    call.id, TextRange(0, call.id.textRange.length)) {

  private fun getResolvedFunction(): Function? {
    call.containingFile.accept(Visitor(FileScope(), object : Visitor.VisitorDelegate() {
      override fun afterVisit(element: PsiElement, scope: Scope): Boolean = element == call

      override fun resolveCall(call: GnCall, function: Function?): Visitor.CallAction =
          when {
            // Template and import calls must be executed so they show up in the scope.
            function is Template ||
                function is Import -> Visitor.CallAction.EXECUTE
            this@GnCallIdentifierReference.call.parents(true)
                .contains(call) -> Visitor.CallAction.VISIT_BLOCK
            else -> Visitor.CallAction.SKIP
          }
    }))
    return call.getUserData(GnKeys.CALL_RESOLVED_FUNCTION)
  }

  fun isTemplate(): Boolean = getResolvedFunction() is TemplateFunction

  override fun resolve(): PsiElement? = getResolvedFunction()?.let {
    if (it !is TemplateFunction) {
      return@let null
    }
    it.declaration
  }
}
