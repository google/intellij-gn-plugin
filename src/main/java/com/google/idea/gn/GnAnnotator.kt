// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.builtin.BuiltinTargetFunction
import com.google.idea.gn.psi.reference.GnCallIdentifierReference
import com.google.idea.gn.psi.scope.FileScope
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

class GnAnnotator : Annotator {

  var file: GnFile? = null

  private fun getCallIdentifierColor(identifier: GnId): GnColors? {
    (identifier.reference as? GnCallIdentifierReference)?.let {
      if (it.isTemplate()) {
        return GnColors.TEMPLATE
      }
    }
    return Builtin.FUNCTIONS[identifier.text]?.let {
      if (it is BuiltinTargetFunction) {
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
      holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
          .textAttributes(it.textAttributesKey).create()
    }
  }

  private fun prepareFile(newFile: GnFile) {
    val visitor = Visitor(FileScope(), object : Visitor.VisitorDelegate() {
      override val observeConditions: Boolean
        get() = false
      override val callTemplates: Boolean
        get() = true
    })
    newFile.accept(visitor)
    file = newFile
  }

  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    if (holder.currentAnnotationSession.file != file) {
      (holder.currentAnnotationSession.file as? GnFile)?.let { prepareFile(it) }
    }
    when (element) {
      is GnId -> annotateIdentifier(element, holder)
    }
  }
}
