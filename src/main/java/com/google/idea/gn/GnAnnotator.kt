// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.Builtin
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnId
import com.google.idea.gn.psi.builtin.TargetFunction
import com.google.idea.gn.psi.reference.GnCallIdentifierReference
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement

class GnAnnotator : Annotator {

  private fun getCallIdentifierColor(identifier: GnId): GnColors? {
    (identifier.reference as? GnCallIdentifierReference)?.let {
      if (it.isTemplate()) {
        return GnColors.TEMPLATE
      }
    }
    return Builtin.FUNCTIONS[identifier.text]?.let {
      if (it is TargetFunction) {
        GnColors.TARGET_FUNCTION
      } else {
        GnColors.BUILTIN_FUNCTION
      }
    }
  }

  private fun annotateIdentifier(identifier: GnId, holder: AnnotationHolder) {
    val parent = identifier.parent
    val color = if (parent is GnCall) {
      getCallIdentifierColor(identifier)
    } else {
      GnColors.VARIABLE
    }
    color?.let {
      val annotation = holder.createInfoAnnotation(identifier, null)
      annotation.textAttributes = it.textAttributesKey
    }
  }

  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    if (element is GnId) {
      annotateIdentifier(element, holder)
    }
  }
}