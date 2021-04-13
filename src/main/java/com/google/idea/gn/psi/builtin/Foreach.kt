// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.scope.Scope

class Foreach : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    val block = call.block
    if (exprList.size != 2 || block == null) {
      return null
    }
    val id = (exprList[0] as? GnPrimaryExpr)?.id?.text ?: return null
    val list = GnPsiUtil.evaluate(exprList[1], targetScope)?.list ?: return null
    val oldValue = targetScope.getVariable(id)
    for (v in list) {
      targetScope.addVariable(Variable(id, v))
      // Visit the statementList directly, foreach does not define a new block scope.
      block.statementList.accept(Visitor(targetScope))
    }
    targetScope.deleteVariable(id)
    oldValue?.let {
      targetScope.addVariable(it)
    }

    return null
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "foreach"
  }
}
