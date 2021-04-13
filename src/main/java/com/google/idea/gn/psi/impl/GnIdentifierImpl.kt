// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.psi.impl

import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.reference.GnCallIdentifierReference
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

abstract class GnIdentifierImpl(node: ASTNode) : ASTWrapperPsiElement(node), PsiElement {
  override fun getReference(): PsiReference? {
    return when (val parent = parent) {
      is GnCall -> GnCallIdentifierReference(parent)
      else -> null
    }
  }
}
