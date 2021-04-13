// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.psi.scope.Scope

class StringJoin : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    if (exprList.size != 2) {
      return null
    }
    val sep = GnPsiUtil.evaluate(exprList[0], targetScope)?.string ?: return null
    val list = GnPsiUtil.evaluate(exprList[1], targetScope)?.list?.mapNotNull { it.string }
        ?: return null
    return GnValue(list.joinToString(sep))
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "string_join"
  }
}
