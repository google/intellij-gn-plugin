//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.builtin.*
import com.intellij.psi.PsiElement

object Builtin {
  const val DEPS = "deps"
  const val PUBLIC_DEPS = "public_deps"
  const val SOURCES = "sources"
  val FUNCTIONS: Map<String, Function?> by lazy {
    arrayOf(Group(), SourceSet(), Executable(), Import(),
        Template()).associateBy { it.name }
  }
  fun isBuiltIn(element: PsiElement): Boolean = FUNCTIONS.containsKey(element.text)
}