// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.GnFilePattern
import com.google.idea.gn.psi.GnExprList
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.psi.scope.Scope


fun executeFilter(_exprList: GnExprList, scope: Scope, exclude: Boolean): GnValue? {
  val exprList = _exprList.exprList
  if (exprList.size != 2) {
    return null
  }
  val list = GnPsiUtil.evaluate(exprList[0], scope)?.list ?: return null
  val patterns = GnPsiUtil.evaluate(exprList[1], scope)?.list?.mapNotNull { v ->
    v.string?.let {
      GnFilePattern(it)
    }
  }
      ?: return null

  return GnValue(list.filter {
    it.string?.let { s ->
      exclude.xor(patterns.any { patt ->
        patt.matches(s)
      })
    } ?: false
  })
}
