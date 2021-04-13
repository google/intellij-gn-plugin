// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.psi.impl

import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.scope.Scope
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode


abstract class GnBinaryExprImpl(node: ASTNode) : ASTWrapperPsiElement(node), GnBinaryExpr {

  override fun evaluate(scope: Scope): GnValue? {
    val list = exprList
    val left = GnPsiUtil.evaluate(list[0], scope) ?: return null
    val right = GnPsiUtil.evaluate(list[1], scope) ?: return null
    return when (this) {
      is GnGtExpr -> left.greaterThan(right)
      is GnGeExpr -> left.greaterThanOrEqual(right)
      is GnLtExpr -> left.lessThan(right)
      is GnLeExpr -> left.lessThanOrEqual(right)
      is GnAndExpr -> left.and(right)
      is GnOrExpr -> left.or(right)
      is GnEqualExpr -> GnValue(left == right)
      is GnNotEqualExpr -> GnValue(left != right)
      is GnPlusExpr -> left.plus(right)
      is GnMinusExpr -> left.minus(right)
      else -> null
    }
  }

}
