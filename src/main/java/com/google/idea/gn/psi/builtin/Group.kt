//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.psi.Builtin
import com.google.idea.gn.psi.FunctionVariable

class Group : TargetFunction() {

  override val name: String
    get() = NAME

  companion object {
    const val NAME = "group"
    val VARIABLES by lazy {
      listOf(Builtin.DEPS, Builtin.PUBLIC_DEPS, Builtin.DATA_DEPS).associateBy { it.name }
    }
  }

  override val variables: Map<String, FunctionVariable> get() = VARIABLES

}