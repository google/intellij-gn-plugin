// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.intellij.lang.Commenter

class GnCommenter : Commenter {
  override fun getCommentedBlockCommentPrefix(): String? = null

  override fun getCommentedBlockCommentSuffix(): String? = null

  override fun getBlockCommentPrefix(): String? = null

  override fun getBlockCommentSuffix(): String? = null

  override fun getLineCommentPrefix(): String? = "#"

}
