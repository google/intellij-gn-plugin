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

class Defined : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val expr = call.exprList.exprList.getOrNull(0) ?: return null
    return GnValue(GnPsiUtil.evaluate(expr, targetScope) != null)
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "defined"
  }
}
