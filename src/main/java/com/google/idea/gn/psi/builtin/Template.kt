// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.GnKeys
import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.scope.Scope

class Template : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val name = GnPsiUtil.evaluateFirstToString(call.exprList, targetScope) ?: return null
    val func = TemplateFunction(name, call, targetScope)
    targetScope.installFunction(func)
    call.putUserData(GnKeys.TEMPLATE_INSTALLED_FUNCTION, func)
    return null
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  override val isBuiltin: Boolean
    get() = true

  override val identifierName: String
    get() = NAME

  override val postInsertType: CompletionIdentifier.PostInsertType?
    get() = CompletionIdentifier.PostInsertType.CALL_WITH_STRING

  companion object {
    const val NAME = "template"
    const val TARGET_NAME = "target_name"
  }
}
