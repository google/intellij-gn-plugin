// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.config

import com.intellij.openapi.Disposable
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class GnProjectConfigurable(project: Project) : GnConfigurableBase(
    project), Configurable.NoScroll, Disposable {

  private var modified: Boolean = false

  override fun isModified() = modified

  override fun getDisplayName(): String {
    return "Gn"
  }

  override fun apply() {
    settings.modify {
      it.projectRoot = projectPathField.text
    }
  }

  private val projectPathField = pathToDirectoryTextField(
      this,
      "Select Project's Gn root",
      onTextChanged = fun() {
        modified = true
      }
  ).apply {
    isEditable = false
    project.gnRoot?.let {
      text = it.path
    }
  }

  override fun createComponent(): JComponent? = layout {
    row("Gn root", projectPathField)
  }

  override fun dispose() {

  }

}
