// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.completion.CompletionIdentifier

class Variable(override val identifierName: String) : CompletionIdentifier {
  constructor(name: String, value: GnValue?) : this(name) {
    this.value = value
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.VARIABLE

  override val typeString: String?
    get() = value?.type?.toString()
  var value: GnValue? = null

}
