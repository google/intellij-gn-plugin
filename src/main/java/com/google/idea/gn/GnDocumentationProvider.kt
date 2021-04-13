// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.GnId
import com.google.idea.gn.util.getPathLabel
import com.intellij.lang.documentation.DocumentationProvider
import com.intellij.psi.PsiElement

class GnDocumentationProvider : DocumentationProvider {

  private fun getQuickIdentifierNavigateInfo(element: PsiElement, originalElement: GnId): String? {
    val file = element.containingFile
    val originalFile = originalElement.containingFile
    if (file != originalFile) {
      return getPathLabel(file)
    }
    return null
  }

  override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? =
      when {
        element != null && originalElement is GnId -> getQuickIdentifierNavigateInfo(element,
            originalElement)
        else -> null
      }

}
