// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project

abstract class GnConfigurableBase(project: Project) : Configurable {

  val settings: GnSettingsService = project.gnSettings

}
