// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.builtin.Template
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope
import com.google.idea.gn.psi.scope.TemplateScope

class TemplateFunction(override val name: String, val declaration: GnCall) : Function() {
  override fun execute(call: GnCall, targetScope: Scope) {
    val declarationBlock = declaration.block ?: return
    val executionScope: BlockScope = TemplateScope(targetScope, call)

    executionScope.addVariable(
        Variable(Template.TARGET_NAME,
            GnPsiUtil.evaluateFirst(call.exprList, targetScope)))

    call.block?.let { callBlock ->
      val innerScope: Scope = BlockScope(targetScope)
      Visitor(innerScope).visitBlock(callBlock)
      innerScope.consolidateVariables()?.let {
        executionScope
            .addVariable(Variable(Template.INVOKER, GnValue(it)))
      }
    }

    Visitor(BlockScope(executionScope)).visitBlock(declarationBlock)
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.TEMPLATE

  override val isBuiltin: Boolean
    get() = false

}