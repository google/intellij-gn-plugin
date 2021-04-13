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

class StringSplit : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    if (exprList.isEmpty()) {
      return null
    }
    val str = GnPsiUtil.evaluate(exprList[0], targetScope)?.string ?: return null
    val sep = exprList.getOrNull(1)?.let { GnPsiUtil.evaluate(it, targetScope)?.string } ?: " "
    return GnValue(str.split(sep).filter { it.isNotEmpty() }.map { GnValue(it) })
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "string_split"
  }
}
