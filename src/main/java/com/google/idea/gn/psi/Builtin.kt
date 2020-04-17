// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.builtin.*

object Builtin {

  val DEPS by lazy { FunctionVariable("deps", GnValue.Type.LIST) }
  val PUBLIC_DEPS by lazy { FunctionVariable("public_deps", GnValue.Type.LIST) }
  val DATA_DEPS by lazy { FunctionVariable("data_deps", GnValue.Type.LIST) }

  val SOURCES by lazy { FunctionVariable("sources", GnValue.Type.LIST) }

  val FUNCTIONS: Map<String, Function> by lazy {
    arrayOf(Group(), SourceSet(), Executable(), Import(),
        Template()).associateBy { it.name }
  }
}