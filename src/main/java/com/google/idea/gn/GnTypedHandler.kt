// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.CodeInsightSettings
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class GnTypedHandler : TypedHandlerDelegate() {
  override fun beforeCharTyped(c: Char, project: Project, editor: Editor,
                               file: PsiFile, fileType: FileType): Result {
    if (GnLanguage != file.language) {
      return Result.CONTINUE
    }
    return if (!CodeInsightSettings.getInstance().AUTOINSERT_PAIR_BRACKET) {
      Result.CONTINUE
    } else Result.CONTINUE
  }

  override fun checkAutoPopup(charTyped: Char, project: Project, editor: Editor,
                              file: PsiFile): Result {
    if (GnLanguage != file.language) {
      return Result.CONTINUE
    }
    if (charTyped == '"' || charTyped == '/' || charTyped == ':') {
      AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
      return Result.STOP
    }
    return Result.CONTINUE
  }
}
