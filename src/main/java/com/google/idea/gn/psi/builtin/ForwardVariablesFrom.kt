// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.scope.Scope

class ForwardVariablesFrom : Function() {

  override fun execute(call: GnCall, targetScope: Scope) {
    // TODO implement variable forwarding
  }

  override val isBuiltin: Boolean
    get() = true
  override val name: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "forward_variables_from"
    const val STAR = "*"
  }
}