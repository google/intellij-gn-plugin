// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object GnFileType : LanguageFileType(GnLanguage) {
  override fun getName(): String {
    return "GN"
  }

  override fun getDescription(): String {
    return "GN Build"
  }

  override fun getDefaultExtension(): String {
    return "gn"
  }

  override fun getIcon(): Icon? {
    return GnIcons.FILE
  }
}
