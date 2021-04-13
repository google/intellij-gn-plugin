// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.psi.Builtin
import com.google.idea.gn.psi.Function

object BuiltinScope : Scope(null) {
  override fun getFunction(name: String): Function? {
    return Builtin.FUNCTIONS[name]
  }

  override fun installFunction(function: Function) = Unit // Can't install functions on BuiltinScope

  override val functions: Sequence<Function>
    get() = Builtin.FUNCTIONS.asSequence().map { it.value }
}
