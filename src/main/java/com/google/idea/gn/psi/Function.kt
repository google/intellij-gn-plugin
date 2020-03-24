//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.scope.Scope

abstract class Function {
  abstract fun execute(call: GnCall,
                       targetScope: Scope)

  abstract val isBuiltin: Boolean
  abstract val name: String
}