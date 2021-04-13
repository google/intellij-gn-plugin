// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.scope.Scope

class ForwardVariablesFrom : Function {

  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    if (exprList.size < 2) {
      return null
    }
    val src = GnPsiUtil.evaluate(exprList[0], targetScope)?.scope ?: return null
    val fwdValue = GnPsiUtil.evaluate(exprList[1], targetScope) ?: return null
    val doForward = when {
      fwdValue.string == "*" -> null
      fwdValue.list != null -> fwdValue.list!!.mapNotNull { it.string }.toSet()
      else -> return null
    }

    val dontForward = if (exprList.size >= 3) {
      GnPsiUtil.evaluate(exprList[2], targetScope)?.list?.mapNotNull { it.string }?.toSet()
          ?: return null
    } else {
      emptySet()
    }

    for (v in src) {
      if ((doForward == null || doForward.contains(v.key)) && !dontForward.contains(v.key)) {
        targetScope.addVariable(Variable(v.key, v.value))
      }
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
    const val NAME = "forward_variables_from"
    const val STAR = "*"
  }
}
