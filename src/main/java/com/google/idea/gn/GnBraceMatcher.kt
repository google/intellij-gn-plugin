// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.Types
import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class GnBraceMatcher : PairedBraceMatcher {
  override fun getPairs(): Array<BracePair> {
    return PAIRS
  }

  override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType,
                                               contextType: IElementType?): Boolean {
    return TokenType.WHITE_SPACE == contextType ||
        isCloseBraceToken(contextType)
  }

  override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int {
    return openingBraceOffset
  }

  companion object {
    private val PAIRS = arrayOf(
        BracePair(Types.OBRACKET, Types.CBRACKET, true),
        BracePair(Types.OPAREN, Types.CPAREN, true),
        BracePair(Types.OBRACE, Types.CBRACE, true))

    private fun isCloseBraceToken(type: IElementType?): Boolean {
      for (pair in PAIRS) {
        if (type === pair.rightBraceType) {
          return true
        }
      }
      return false
    }
  }
}
