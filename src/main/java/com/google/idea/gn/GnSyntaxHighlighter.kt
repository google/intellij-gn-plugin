// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.Types
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class GnSyntaxHighlighter : SyntaxHighlighterBase() {
  override fun getHighlightingLexer(): Lexer {
    return GnLexerAdapter()
  }

  override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
    return arrayOf(when (tokenType) {
      Types.QUOTE,
      Types.STRING_LITERAL -> GnColors.STRING
      Types.COMMENT -> GnColors.COMMENT
      Types.AND,
      Types.OR,
      Types.EQUAL,
      Types.EQUAL_EQUAL,
      Types.NOT_EQUAL,
      Types.LESSER,
      Types.LESSER_EQUAL,
      Types.GREATER,
      Types.GREATER_EQUAL,
      Types.UNARY_NOT,
      Types.PLUS,
      Types.MINUS,
      Types.PLUS_EQUAL,
      Types.MINUS_EQUAL -> GnColors.OPERATOR
      Types.IF,
      Types.ELSE,
      Types.TRUE,
      Types.FALSE -> GnColors.KEYWORD
      Types.INTEGRAL_LITERAL -> GnColors.INT
      Types.OBRACE, Types.CBRACE -> GnColors.BRACES
      Types.OPAREN, Types.CPAREN -> GnColors.PARENS
      Types.OBRACKET, Types.CBRACKET -> GnColors.BRACKETS
      Types.DOT -> GnColors.DOT
      Types.DOLLAR -> GnColors.VARIABLE
      else -> return emptyArray()
    }.textAttributesKey)
  }

}
