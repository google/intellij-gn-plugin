// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.config

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile

interface GnSettingsService {
  data class State(
      var version: Int? = null,
      var projectRoot: String? = null
  )

  fun modify(action: (State) -> Unit)

  val projectRoot: String?
}

val Project.gnSettings: GnSettingsService
  get() = getService(GnSettingsService::class.java)
      ?: error("Failed to get GnSettingsService for $this")

val Project.gnRoot: VirtualFile?
  get() =
    gnSettings.projectRoot?.let {
      LocalFileSystem.getInstance().findFileByPath(it)?.let { f ->
        if (f.isDirectory) {
          f
        } else {
          null
        }
      }
    } ?: guessProjectDir()
