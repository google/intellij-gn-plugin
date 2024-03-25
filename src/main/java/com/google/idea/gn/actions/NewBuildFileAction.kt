// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.actions

import com.google.idea.gn.psi.GnFile
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.actionSystem.*
import com.intellij.util.containers.getIfSingle
import com.intellij.util.containers.stream


class NewBuildFileAction : AnAction() {


  private fun isAvailable(dataContext: DataContext): Boolean {
    // Only show action if a BUILD.gn file does not exist.
    val view = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return false
    val dir = view.directories.stream().getIfSingle() ?: return false
    return dir.virtualFile.findChild(GnFile.BUILD_FILE) == null
  }

  override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

  override fun update(e: AnActionEvent) {
    val dataContext = e.dataContext
    val presentation = e.presentation
    presentation.isEnabledAndVisible = isAvailable(dataContext)
  }


  override fun actionPerformed(e: AnActionEvent) {
    val dataContext = e.dataContext
    val view = LangDataKeys.IDE_VIEW.getData(dataContext) ?: return
    val project = CommonDataKeys.PROJECT.getData(dataContext) ?: return
    val dir = view.directories.stream().getIfSingle() ?: return
    val template = FileTemplateManager.getInstance(project)
        .getInternalTemplate(GnFile.TEMPLATE_NAME)

    CreateFileFromTemplateAction.createFileFromTemplate(GnFile.BUILD_FILE, template, dir, null,
        true)
  }
}
