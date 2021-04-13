// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.parser.GnParser
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.Types
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class GnParserDefinition : ParserDefinition {
  override fun createLexer(project: Project): Lexer {
    return GnLexerAdapter()
  }

  override fun createParser(project: Project): PsiParser {
    return GnParser()
  }

  override fun getFileNodeType(): IFileElementType {
    return FILE
  }

  override fun getCommentTokens(): TokenSet {
    return COMMENTS
  }

  override fun getWhitespaceTokens(): TokenSet {
    return WHITE_SPACES
  }

  override fun getStringLiteralElements(): TokenSet {
    return TokenSet.EMPTY
  }

  override fun createElement(node: ASTNode): PsiElement {
    return Types.Factory.createElement(node)
  }

  override fun createFile(viewProvider: FileViewProvider): PsiFile {
    return GnFile(viewProvider)
  }

  companion object {
    val WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE)
    val COMMENTS = TokenSet.create(Types.COMMENT)
    val STRING_EXPR = TokenSet.create(Types.QUOTE, Types.STRING_LITERAL)
    val FILE = IFileElementType(GnLanguage)
  }
}
