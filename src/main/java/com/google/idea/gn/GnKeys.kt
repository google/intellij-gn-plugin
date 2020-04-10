//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.Function
import com.intellij.openapi.util.Key

object GnKeys {
  val LOOKUP_ITEM_TYPE: Key<GnCompletionContributor.CompleteType> = Key("com.google.idea.gn.LOOKUP_ITEM_TYPE")
  val CALL_RESOLVED_FUNCTION: Key<Function> = Key("com.google.idea.gn.CALL_RESOLVED_FUNCTION")
}