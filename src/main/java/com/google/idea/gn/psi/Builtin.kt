// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.builtin.BuiltinTargetFunction
import com.google.idea.gn.psi.builtin.ForwardVariablesFrom
import com.google.idea.gn.psi.builtin.Import
import com.google.idea.gn.psi.builtin.Template

object Builtin {

  val FUNCTIONS: Map<String, Function> by lazy {
    BuiltinTargetFunction.values().asSequence().plus(
        sequenceOf(Import(),
            Template(),
            ForwardVariablesFrom()
        ))
        .associateBy { it.identifierName }
  }
}