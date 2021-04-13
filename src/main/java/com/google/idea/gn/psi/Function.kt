// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.scope.Scope

interface Function : CompletionIdentifier {
  fun execute(call: GnCall,
              targetScope: Scope): GnValue?

  val isBuiltin: Boolean
  val variables: Map<String, FunctionVariable> get() = emptyMap()

  override val postInsertType: CompletionIdentifier.PostInsertType?
    get() = CompletionIdentifier.PostInsertType.CALL

  override fun gatherChildren(operator: (CompletionIdentifier) -> Unit) =
      variables.forEach { operator(it.value) }
}
