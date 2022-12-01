// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.GnFormatBlock
import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings

class GnFormattingBuilder : FormattingModelBuilder {
  override fun createModel(formattingContext: FormattingContext): FormattingModel {
    val element = formattingContext.psiElement
    val settings = formattingContext.codeStyleSettings
    return FormattingModelProvider
        .createFormattingModelForPsiFile(element.containingFile,
            GnFormatBlock(element.node,
                Alignment.createAlignment(),
                createSpaceBuilder(settings)),
            settings)
  }

  override fun getRangeAffectingIndent(file: PsiFile, offset: Int, elementAtOffset: ASTNode): TextRange? {
    return null
  }

  companion object {
    private fun createSpaceBuilder(settings: CodeStyleSettings): SpacingBuilder {
      return SpacingBuilder(settings,
          GnLanguage) // .around(Types.ASSIGN_OP).spacing(1, 1, 0, false, 0)
// .between(Types.CPAREN, Types.BLOCK).spacing(1, 1, 0, false, 0)
// .between(Types.STATEMENT, Types.STATEMENT).spacing(0, 0, 1, false, 1)
// .afterInside(Types.COMMA, Types.COLLECTION).spacing(0, 0, 1, false, 0)
// .after(Types.OBRACE).spacing(0, 0, 1, false, 1)
    }
  }
}
