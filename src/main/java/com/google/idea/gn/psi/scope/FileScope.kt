// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.psi.Target
import java.util.*

class FileScope : BlockScope(BuiltinScope) {
  override val targets: Map<String, Target>?
    get() = _targets

  override fun addTarget(target: Target) {
    if (_targets == null) {
      _targets = HashMap()
    }
    _targets!![target.name] = target
  }

  private var _targets: MutableMap<String, Target>? = null
}
