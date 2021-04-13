// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnValue

open class BlockScope(parent: Scope?) : Scope(parent) {
  override fun getFunction(name: String): Function? {
    return installedFunctions[name] ?: super.getFunction(name)
  }

  override fun installFunction(function: Function) {
    installedFunctions[function.identifierName] = function
  }

  fun intoValue(): GnValue? {
    return consolidateVariables()?.let { GnValue(it) } ?: GnValue(emptyMap())
  }

  override val functions: Sequence<Function>
    get() = installedFunctions.asSequence().map { it.value }

  private val installedFunctions: MutableMap<String, Function> = HashMap()
}
