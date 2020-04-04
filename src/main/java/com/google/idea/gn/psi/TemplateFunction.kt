//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.builtin.Template
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope
import com.google.idea.gn.psi.scope.TemplateScope

class TemplateFunction(override val name: String, private val block: GnBlock) : Function() {
  override fun execute(call: GnCall, targetScope: Scope) {
    val executionScope: BlockScope = TemplateScope(targetScope, call)

    executionScope.addVariable(
        Variable(Template.TARGET_NAME,
            GnPsiUtil.evaluateFirst(call.exprList, targetScope)))

    call.block?.let { block ->
      val innerScope: Scope = BlockScope(targetScope)
      Visitor(innerScope).visitBlock(block)
      innerScope.consolidateVariables()?.let {
        executionScope
            .addVariable(Variable(Template.INVOKER, GnValue(it)))
      }
    }

    Visitor(BlockScope(executionScope)).visitBlock(this.block)
  }

  override val isBuiltin: Boolean
    get() = false

}