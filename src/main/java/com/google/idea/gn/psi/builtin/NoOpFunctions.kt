// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.psi.scope.Scope

// Builtin functions that will be no-ops when executing.
enum class NoOpFunctions(override val identifierName: String) : Function {
  ASSERT("assert"),
  EXEC_SCRIPT("exec_script"),
  NOT_NEEDED("not_needed"),
  POOL("pool"),
  PRINT("print"),
  SET_DEFAULTS("set_defaults"),
  SET_SOURCES_ASSIGNMENT_FILTER("set_sources_assignment_filter"),
  TOOL("tool"),
  TOOLCHAIN("toolchain"),
  WRITE_FILE("write_file");

  override fun execute(call: GnCall, targetScope: Scope): GnValue? = null

  override val isBuiltin: Boolean
    get() = true
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION
}
