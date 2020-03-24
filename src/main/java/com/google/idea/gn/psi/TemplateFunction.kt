//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.builtin.Template
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope
import com.google.idea.gn.psi.scope.TemplateScope

class TemplateFunction(override val name: String, private val mBlock: GnBlock) : Function() {
  override fun execute(call: GnCall, targetScope: Scope) {
    val executionScope: BlockScope = TemplateScope(targetScope, call)
    executionScope.addVariable(
        Variable(Template.TARGET_NAME,
            GnPsiUtil.evaluateFirst(call.exprList, targetScope)))
    val block = call.block
    if (block != null) {
      val innerScope: Scope = BlockScope(targetScope)
      Visitor(innerScope).visitBlock(block)
      if (innerScope.variables != null) {
        executionScope
            .addVariable(Variable(Template.INVOKER, GnValue(innerScope.variables)))
      }
    }
    Visitor(executionScope).visitBlock(mBlock)
  }

  override val isBuiltin: Boolean
    get() = false

}