// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.completion.FileCompletionProvider
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.TemplateFunction
import com.intellij.openapi.util.Key

object GnKeys {
  val LABEL_COMPLETION_TYPE: Key<FileCompletionProvider.CompleteType> = Key(
      "com.google.idea.gn.LABEL_COMPLETION_TYPE")
  val IDENTIFIER_COMPLETION_TYPE: Key<CompletionIdentifier.IdentifierType> = Key(
      "com.google.idea.gn.IDENTIFIER_COMPLETION_TYPE")
  val CALL_RESOLVED_FUNCTION: Key<Function> = Key("com.google.idea.gn.CALL_RESOLVED_FUNCTION")
  val TEMPLATE_INSTALLED_FUNCTION: Key<TemplateFunction> = Key(
      "com.google.idea.gn.TEMPLATE_INSTALLED_FUNCTION")
}
