// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.reference

import com.google.idea.gn.GnLabel
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.GnPsiUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReferenceBase

class GnLabelReference(element: PsiElement, private val label: GnLabel) : PsiReferenceBase<PsiElement?>(
    element, TextRange(1, element.textRange.length - 1)) {
  override fun resolve(): PsiElement? {
    val file = GnPsiUtil.findPsiFile(
        element.containingFile, label)
    val labelTarget = label.target
    if (file !is GnFile || labelTarget == null) {
      return file
    }
    if (file.name != GnFile.BUILD_FILE) {
      return file
    }
    val scope = file.scope
    val t = label.target?.let { scope.getTarget(it) }
    return if (t?.call == null)
      file
    else t.call
  }
}
