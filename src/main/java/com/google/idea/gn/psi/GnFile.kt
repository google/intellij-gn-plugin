// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.GnFileType
import com.google.idea.gn.GnLanguage
import com.google.idea.gn.psi.scope.FileScope
import com.google.idea.gn.psi.scope.Scope
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.Key
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager

class GnFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, GnLanguage) {
  override fun getFileType(): FileType {
    return GnFileType
  }

  override fun toString(): String {
    val vf = virtualFile
    return if (vf != null) {
      "GN File " + vf.path
    } else "GN File"
  }

  val scope: Scope
    get() = CachedValuesManager.getCachedValue(this, CACHE_KEY) {
      val fileScope = buildScope()
      CachedValueProvider.Result(fileScope, this)
    }

  fun buildScope(injected: Map<String, GnValue>? = null): Scope {
    val fileScope = FileScope()
    injected?.let {
      for ((k, v) in it) {
        fileScope.addVariable(Variable(k, v))
      }
    }
    accept(Visitor(fileScope))
    return fileScope
  }

  companion object {
    const val BUILD_FILE = "BUILD.gn"
    const val GNI = "gni"
    const val TEMPLATE_NAME = "Gn File"
    val CACHE_KEY = Key<CachedValue<Scope>>("gn-file-scope")
  }
}
