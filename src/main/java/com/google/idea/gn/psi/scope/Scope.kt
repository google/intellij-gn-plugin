// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.Target
import java.util.*

abstract class Scope protected constructor(val parent: Scope?) {
  open fun getFunction(name: String): Function? {
    return parent?.getFunction(name)
  }

  abstract fun installFunction(function: Function)

  abstract val functions: Sequence<Function>

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
    _variables!![v.identifierName] = v
  }

  open fun deleteVariable(id: String) {
    _variables?.remove(id)
  }

  open fun getVariable(name: String): Variable? {
    return _variables?.get(name) ?: parent?.getVariable(name)
  }

  val variables: Map<String, Variable>?
    get() = _variables

  open val callSite: GnCall?
    get() = parent?.callSite

  fun consolidateVariables(): Map<String, GnValue>? {
    val map = variables?.filterValues { it.value != null }?.mapValues { it.value.value!! }
    if (map.isNullOrEmpty()) {
      return null
    }
    return map
  }

  fun gatherCompletionIdentifiers(operator: (CompletionIdentifier) -> Unit) {
    parent?.gatherCompletionIdentifiers(operator)
    functions.forEach {
      operator(it)
    }
    variables?.forEach {
      operator(it.value)
    }
  }

  override fun toString(): String {
    return "$parent{Scope(variables: ${variables?.values?.joinToString { it.identifierName }}, functions:${functions.joinToString { it.identifierName }})}"
  }

  private var _variables: MutableMap<String, Variable>? = null


}
