//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.psi.Function

open class BlockScope(parent: Scope?) : Scope(parent) {
  override fun getFunction(name: String): Function? {
    return functions[name] ?: super.getFunction(name)
  }

  override fun installFunction(function: Function) {
    functions[function.name] = function
  }

  private val functions: MutableMap<String, Function> = HashMap()
}