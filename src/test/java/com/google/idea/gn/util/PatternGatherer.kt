// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.util

import com.intellij.patterns.ElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor


class PatternGatherer(val patterns: Map<String, ElementPattern<out PsiElement>>) {

  constructor(vararg patterns: Pair<String, ElementPattern<out PsiElement>>) : this(
      patterns.toMap())

  fun gather(root: PsiElement): Map<String, PsiElement> {
    val result = mutableMapOf<String, PsiElement>()
    root.accept(object : PsiElementVisitor() {
      override fun visitElement(element: PsiElement) {
        for (p in patterns) {
          if (p.value.accepts(element)) {
            if (result.containsKey(p.key)) {
              throw RuntimeException(
                  "Found duplicate element (${result[p.key]}/$element) for pattern ${p.value}")
            }
            result[p.key] = element
          }
        }
        element.children.forEach {
          if (result.size == patterns.size) {
            return
          }
          it.accept(this)
        }
      }
    })
    return result
  }
}
