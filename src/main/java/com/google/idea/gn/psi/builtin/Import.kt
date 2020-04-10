//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.GnLabel
import com.google.idea.gn.GnLanguage
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.Visitor
import com.google.idea.gn.psi.scope.Scope

class Import : Function() {
  override fun execute(call: GnCall, targetScope: Scope) {
    val name = GnPsiUtil.evaluateFirstToString(call.exprList, targetScope) ?: return
    val label: GnLabel = GnLabel.parse(name) ?: return
    val file = GnPsiUtil.findPsiFile(call.containingFile, label)
    if (file == null || GnLanguage != file.language) {
      return
    }
    file.accept(Visitor(targetScope))
  }

  override val isBuiltin: Boolean
    get() = true

  override val name: String
    get() = NAME


  override val insertionText: String
    get() = "$name(\"\")"
  override val caretShift: Int
    get() = -2
  override val autoSuggestOnInsertion: Boolean
    get() = true

  companion object {
    const val NAME = "import"
  }
}