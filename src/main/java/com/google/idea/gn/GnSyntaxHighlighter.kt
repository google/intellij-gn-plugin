//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.Types
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class GnSyntaxHighlighter : SyntaxHighlighterBase() {
  override fun getHighlightingLexer(): Lexer {
    return GnLexerAdapter()
  }

  override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> =
      when (tokenType) {
        Types.STRING_LITERAL -> STRING_KEYS
        Types.CALL -> FUNCTION_CALL_KEYS
        Types.COMMENT -> COMMENT_KEYS
        Types.IDENTIFIER -> IDENTIFIER_KEYS
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
        Types.MINUS_EQUAL -> OPERATOR_KEYS
        Types.IF,
        Types.ELSE,
        Types.TRUE,
        Types.FALSE -> KEYWORD_KEYS
        else -> EMPTY_KEYS
      }


  companion object {
    val COMMENT = TextAttributesKey.createTextAttributesKey("GN_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
    val STRING = TextAttributesKey.createTextAttributesKey("GN_STRING", DefaultLanguageHighlighterColors.STRING)
    val IDENTIFER = TextAttributesKey.createTextAttributesKey("GN_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)
    val BUILTIN_IDENTIFIER = TextAttributesKey.createTextAttributesKey("GN_BUILTIN_IDENTIFIER",
        DefaultLanguageHighlighterColors.KEYWORD)
    val KEYWORD = TextAttributesKey.createTextAttributesKey("GN_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
    val OPERATOR = TextAttributesKey.createTextAttributesKey("GN_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)
    val FUNCTION_CALL = TextAttributesKey.createTextAttributesKey("GN_CALL", DefaultLanguageHighlighterColors.FUNCTION_CALL)
    val COMMENT_KEYS = arrayOf(COMMENT)
    val STRING_KEYS = arrayOf(STRING)
    val KEYWORD_KEYS = arrayOf(KEYWORD)
    val IDENTIFIER_KEYS = arrayOf(IDENTIFER)
    val OPERATOR_KEYS = arrayOf(OPERATOR)
    val FUNCTION_CALL_KEYS = arrayOf(
        FUNCTION_CALL)
    val EMPTY_KEYS = emptyArray<TextAttributesKey>()
  }
}