//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.Builtin
import com.google.idea.gn.psi.GnAssignment
import com.google.idea.gn.psi.GnCall
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.PsiElement

class GnAnnotator : Annotator {
  private fun annotateCall(call: GnCall, holder: AnnotationHolder) {
    val identifier: PsiElement = call.id
    if (Builtin.isBuiltIn(identifier)) {
      val annotation = holder.createInfoAnnotation(identifier, null)
      annotation.textAttributes = GnSyntaxHighlighter.BUILTIN_IDENTIFIER
    }
  }

  private fun annotateAssignment(assignment: GnAssignment, holder: AnnotationHolder) {
    val lvalue = assignment.lvalue
    val id = lvalue.id
    if (id != null) {
      val a = holder.createInfoAnnotation(id, null)
      a.textAttributes = DefaultLanguageHighlighterColors.INSTANCE_FIELD
    }
  }

  override fun annotate(element: PsiElement, holder: AnnotationHolder) {
    if (element is GnCall) {
      annotateCall(element, holder)
    } else if (element is GnAssignment) {
      annotateAssignment(element, holder)
    }
  }
}