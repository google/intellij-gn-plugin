// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.builtin.*

object Builtin {

  val FUNCTIONS: Map<String, Function> by lazy {
    sequenceOf(
        DeclareArgs(),
        Defined(),
        FilterExclude(),
        FilterInclude(),
        Foreach(),
        ForwardVariablesFrom(),
        GetLabelInfo(),
        GetPathInfo(),
        GetTargetOutputs(),
        GetEnv(),
        Import(),
        ProcessFileTemplate(),
        ReadFile(),
        RebasePath(),
        SetDefaultToolchain(),
        SplitList(),
        StringJoin(),
        StringReplace(),
        StringSplit(),
        Template(),
        com.google.idea.gn.psi.builtin.Target()
    ).plus(BuiltinTargetFunction.values().asSequence())
        .plus(NoOpFunctions.values().asSequence())
        .associateBy { it.identifierName }
  }
}
