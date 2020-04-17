//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn.util

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.testFramework.LightPlatformCodeInsightTestCase

abstract class GnCodeInsightTestCase : LightPlatformCodeInsightTestCase() {
  override fun getTestDataPath() = "src/test/testData/project/"

  fun copyTestFiles(filter: (VirtualFile) -> Boolean) {
    runWriteAction {
      val projDir = project.guessProjectDir()!!
      VfsUtil.copyDirectory(this, getVirtualFile(""), projDir, filter)
      // Delete any empty directories.
      VfsUtil.visitChildrenRecursively(projDir, object : VirtualFileVisitor<Unit>() {
        override fun visitFile(file: VirtualFile): Boolean {
          return if (file.isDirectory && file.children.isEmpty()) {
            file.delete(this)
            false
          } else {
            true
          }
        }
      })
    }
  }

  fun copyTestFilesByPath(filter: (String) -> Boolean) {
    copyTestFiles {
      it.isDirectory || filter(VfsUtil.getRelativePath(it, getVirtualFile(""))!!)
    }
  }

}