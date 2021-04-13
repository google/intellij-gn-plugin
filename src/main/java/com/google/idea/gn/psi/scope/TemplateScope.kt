// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.scope

import com.google.idea.gn.psi.GnCall

class TemplateScope(parent: Scope?, var call: GnCall) : BlockScope(parent) {
  override val callSite: GnCall?
    get() = call

}
