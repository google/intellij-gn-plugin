// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.scope.Scope
import com.intellij.extapi.psi.ASTWrapperPsiElement


class SkipFirstArgumentCall(val call: GnCall) : ASTWrapperPsiElement(
    call.node), GnCall {
  override fun getId(): GnId = call.id

  override fun getExprList(): GnExprList = object : GnExprList, ASTWrapperPsiElement(
      call.exprList.node) {
    override fun getExprList(): List<GnExpr> = call.exprList.exprList.drop(1)
  }

  override fun getBlock(): GnBlock? = call.block

}

class Target : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val args = call.exprList.exprList
    if (args.size != 2) {
      return null
    }
    val functionName = GnPsiUtil.evaluate(args[0], targetScope)?.string
        ?: return null
    val function = targetScope.getFunction(functionName) ?: return null
    return function.execute(SkipFirstArgumentCall(call), targetScope)
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  override val isBuiltin: Boolean
    get() = true

  override val identifierName: String
    get() = NAME

  companion object {
    const val NAME = "target"
  }
}
