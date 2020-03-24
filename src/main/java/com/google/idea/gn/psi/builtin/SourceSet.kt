//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

class SourceSet : TargetFunction() {

  override val name: String
    get() = NAME

  companion object {
    const val NAME = "source_set"
  }
}