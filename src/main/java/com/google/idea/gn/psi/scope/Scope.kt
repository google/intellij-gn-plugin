//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.Target
import com.google.idea.gn.psi.Variable
import java.util.*

abstract class Scope protected constructor(val parent: Scope?) {
  open fun getFunction(name: String): Function? {
    return parent?.getFunction(name)
  }

  abstract fun installFunction(function: Function)

  open val targets: Map<String, Target>?
    get() = parent?.targets

  open fun getTarget(name: String): Target? {
    val targets = targets ?: return null
    return targets[name]
  }

  open fun addTarget(target: Target) {
    parent?.addTarget(target)
  }

  open fun addVariable(v: Variable) {
    if (_variables == null) {
      _variables = HashMap()
    }
    _variables!![v.name] = v
  }

  open fun getVariable(name: String): Variable? {
    return _variables?.get(name) ?: parent?.getVariable(name)
  }

  val variables: Map<String, Variable>?
    get() = _variables

  open val callSite: GnCall?
    get() = parent?.callSite

  private var _variables: MutableMap<String, Variable>? = null

}