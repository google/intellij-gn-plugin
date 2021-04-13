// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.GnLabel
import com.google.idea.gn.GnLanguage
import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.scope.Scope

class Import : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val name = GnPsiUtil.evaluateFirstToString(call.exprList, targetScope) ?: return null
    val label: GnLabel = GnLabel.parse(name) ?: return null
    val file = GnPsiUtil.findPsiFile(call.containingFile, label)
    if (file == null || GnLanguage != file.language) {
      return null
    }
    file.accept(Visitor(targetScope))

    return null
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  override val isBuiltin: Boolean
    get() = true

  override val identifierName: String
    get() = NAME

  override val autoSuggestOnInsertion: Boolean
    get() = true

  override val postInsertType: CompletionIdentifier.PostInsertType?
    get() = CompletionIdentifier.PostInsertType.CALL_WITH_STRING

  companion object {
    const val NAME = "import"
  }
}
