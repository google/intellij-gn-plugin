// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.psi

import com.google.idea.gn.completion.CompletionIdentifier

interface FunctionVariable : CompletionIdentifier {
  val type: GnCompositeType? get() = null

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION_VARIABLE

  override val typeString: String?
    get() = type?.toString()
}
