// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.util

import com.google.idea.gn.config.gnRoot
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.GnPsiUtil
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.testFramework.LightPlatformCodeInsightTestCase
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory
import com.intellij.testFramework.fixtures.TempDirTestFixture
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import com.intellij.testFramework.fixtures.impl.LightTempDirTestFixtureImpl

abstract class GnCodeInsightTestCase : LightPlatformCodeInsightTestCase() {

  override fun setUp() {
    val factory = IdeaTestFixtureFactory.getFixtureFactory()
    val fixtureBuilder = factory.createLightFixtureBuilder(projectDescriptor, "gnCodeInsightTest")
    myFixture = CodeInsightTestFixtureImpl(fixtureBuilder.fixture, getTempDirFixture())
    myFixture.setUp();
  }

  protected lateinit var myFixture :CodeInsightTestFixtureImpl;

  val LOGGER = Logger.getInstance(GnPsiUtil.javaClass)

  override fun getTestDataPath() = "src/test/testData/project/"

  val gnFile: GnFile get() = file as GnFile

  fun getProjectFile(path: String): VirtualFile? =
      project.gnRoot!!.findFileByRelativePath(path)

  fun getProjectPsiFile(path: String): PsiFile? = getProjectFile(
      path)?.let { PsiManager.getInstance(project).findFile(it) }

  fun copyTestFilesByVirtualFile(filter: (VirtualFile) -> Boolean) {
    runWriteAction {
      val projDir = project.gnRoot!!
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
    copyTestFilesByVirtualFile {
      it.isDirectory || filter(VfsUtil.getRelativePath(it, getVirtualFile(""))!!)
    }
  }

  fun copyTestFiles(vararg files: String) {
    val set = files.toSet()
    copyTestFilesByPath {
      set.contains(it)
    }
  }

  protected fun getTempDirFixture(): TempDirTestFixture {
    val policy = IdeaTestExecutionPolicy.current()
    return if (policy != null) policy.createTempDirTestFixture() else LightTempDirTestFixtureImpl(true)
  }
}
