//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.TemplateFunction
import com.google.idea.gn.psi.scope.Scope

class Template : Function() {
  override fun execute(call: GnCall, targetScope: Scope) {
    val block = call.block ?: return
    val name = GnPsiUtil.evaluateFirstToString(call.exprList, targetScope) ?: return
    targetScope.installFunction(TemplateFunction(name, block))
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  override val isBuiltin: Boolean
    get() = true

  override val name: String
    get() = NAME

  companion object {
    const val NAME = "template"
    const val TARGET_NAME = "target_name"
    const val INVOKER = "invoker"
  }
}