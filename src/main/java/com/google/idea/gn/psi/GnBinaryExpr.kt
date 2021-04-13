// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.psi

import com.google.idea.gn.psi.scope.Scope

interface GnBinaryExpr {

  fun evaluate(scope: Scope): GnValue?
  val exprList: List<GnExpr>
}
