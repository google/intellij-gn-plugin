// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.util

import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

fun Map<String, PsiElement>.resolvedReference(tag: String): PsiElement? =
    this[tag]?.reference?.resolve()

fun PsiFile.findElementMatching(pattern: ElementPattern<out PsiElement>): PsiElement? {
  val visitor = object : PsiElementVisitor() {
    var result: PsiElement? = null
    override fun visitElement(element: PsiElement) {
      when {
        result != null -> return
        pattern.accepts(element) -> {
          result = element
        }
        else -> {
          element.children.forEach { it.accept(this) }
        }
      }
    }
  }
  this.accept(visitor)
  return visitor.result
}
