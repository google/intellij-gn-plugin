// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.util.GnCodeInsightTestCase
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor

// Verifies code in base test class
class BaseTest : GnCodeInsightTestCase() {
  fun testPathIsClear() {
    copyTestFilesByPath {
      when (it) {
        "build/rules.gni",
        "src/BUILD.gn" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """# Nothing""")

    val collected = mutableSetOf<String>()
    val dir = file.containingDirectory.virtualFile
    VfsUtil.visitChildrenRecursively(dir, object : VirtualFileVisitor<Unit>() {
      override fun visitFile(file: VirtualFile): Boolean {
        if (!file.isDirectory) {
          collected.add(VfsUtil.getRelativePath(file, dir)!!)
        }
        return true
      }
    })
    assertEquals(setOf(GnFile.BUILD_FILE, "build/rules.gni", "src/BUILD.gn"), collected)
  }
}
