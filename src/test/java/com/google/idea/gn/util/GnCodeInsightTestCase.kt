//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn.util

import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightPlatformCodeInsightTestCase

abstract class GnCodeInsightTestCase : LightPlatformCodeInsightTestCase() {
  override fun getTestDataPath() = "src/test/testData/project/"

  fun copyTestFiles(filter: (VirtualFile) -> Boolean) {
    runWriteAction {
      VfsUtil.copyDirectory(this, getVirtualFile(""), project.guessProjectDir()!!, filter)
    }
  }

  fun copyTestFilesByPath(filter: (String) -> Boolean) {
    copyTestFiles { filter(VfsUtil.getRelativePath(it, getVirtualFile(""))!!) }
  }

}