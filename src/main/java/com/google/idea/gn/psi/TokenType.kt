// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.GnLanguage
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class TokenType(@NonNls debugName: String) : IElementType(debugName, GnLanguage) {
      override fun toString(): String =
          when (this) {
                Types.COMMA -> ","
                Types.CBRACKET -> "]"
                Types.OBRACKET -> "["
                Types.OPAREN -> "("
                Types.CPAREN -> ")"
                Types.OBRACE -> "{"
                Types.CBRACE -> ""
                Types.COMMENT -> "Comment"
                Types.DOT -> "."
                Types.ELSE -> "else"
                Types.EQUAL -> "="
                Types.EQUAL_EQUAL -> "=="
                Types.FALSE -> "false"
                Types.GREATER -> ">"
                Types.GREATER_EQUAL -> ">="
                Types.IDENTIFIER -> "identifier"
                Types.IF -> "if"
                Types.INTEGRAL_LITERAL -> "Integral literal"
                Types.LESSER -> "<"
                Types.LESSER_EQUAL -> "<="
                Types.MINUS -> "-"
                Types.MINUS_EQUAL -> "-="
                Types.NOT_EQUAL -> "!="
                Types.OR -> "||"
                Types.PLUS -> "+"
                Types.PLUS_EQUAL -> "+="
                Types.STRING_LITERAL -> "String literal"
                Types.TRUE -> "true"
                Types.UNARY_NOT -> "!"
                Types.DOLLAR -> "$"
        Types.HEX_BYTE -> "Hex expression"
        Types.QUOTE -> "quote"
        else -> "TokenType." + super.toString()
      }

  companion object {
    val WHITE_SPACE: IElementType = TokenType.WHITE_SPACE
  }
}
