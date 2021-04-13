// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope

class DeclareArgs : Function {

  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    if (call.exprList.exprList.size != 0) {
      return null
    }

    val scope = BlockScope(targetScope)
    call.block?.accept(Visitor(scope))

    // NOTE we don't support overrides here, just install all variables with their default values

    scope.consolidateVariables()?.let {
      for (v in it) {
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
    const val NAME = "declare_args"
  }
}
