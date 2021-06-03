// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.util

import com.google.idea.gn.config.gnRoot
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.io.StringWriter


fun getPathLabel(file: VirtualFile, base: VirtualFile, absolute: Boolean = true): String? =
    VfsUtilCore.getRelativePath(file, base)?.let { relative ->
      val writer = StringWriter()
      if (absolute) {
        writer.write("//")
      }
      writer.write(relative)
      writer.toString()
    }


fun getPathLabel(file: VirtualFile, project: Project): String? = project.gnRoot
    ?.let { getPathLabel(file, it) }

fun getPathLabel(file: PsiFile): String? = file.virtualFile?.parent
    ?.let { getPathLabel(it, file.project) }
