// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.completion

import com.intellij.codeInsight.completion.CompletionResultSet

class IdentifierCompletionResultSet(private val resultSet: CompletionResultSet) {
  private val added = mutableSetOf<String>()

  fun addIdentifier(id: CompletionIdentifier) {
    if (!added.contains(id.identifierName)) {
      added.add(id.identifierName)
      id.addToResult(resultSet)
    }
  }
}
