// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.impl

import com.google.idea.gn.GnLabel
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.GnStringExpr
import com.google.idea.gn.psi.reference.GnLabelReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiReference

abstract class GnLiteralReferenceImpl(node: ASTNode) : ASTWrapperPsiElement(node), GnStringExpr {
  override fun getReference(): PsiReference? {
    val scope = when (val file = containingFile) {
      is GnFile -> file.scope
      else -> return null
    }
    val value = GnPsiUtil.evaluate(this, scope)?.string ?: return null
    val label: GnLabel = GnLabel.parse(value)
        ?: return null
    return GnLabelReference(this, label)
  }
}
