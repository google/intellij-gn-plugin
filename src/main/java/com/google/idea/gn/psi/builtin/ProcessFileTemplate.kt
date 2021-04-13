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

class ProcessFileTemplate : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    // TODO do we need a complete implementation here?
    val exprList = call.exprList.exprList
    if (exprList.size < 2) {
      return null
    }
    // Just return the expected number of outputs for now.
    val inputs = GnPsiUtil.evaluate(exprList[0], targetScope)?.list?.mapNotNull { it.string }
        ?: return null
    val outputs = GnPsiUtil.evaluate(exprList[1], targetScope)?.list?.mapNotNull { it.string }
        ?: return null
    val result = mutableListOf<GnValue>()
    for (i in inputs) {
      for (o in outputs) {
        result.add(GnValue(o.replace(Regex("\\{.*}"), i)))
      }
    }
    return GnValue(result)
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "process_file_template"
  }
}
