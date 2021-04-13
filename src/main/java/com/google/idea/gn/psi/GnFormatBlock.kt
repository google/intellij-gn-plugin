// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock
import java.util.*

class GnFormatBlock @JvmOverloads constructor(node: ASTNode, alignment: Alignment?,
                                              private val mSpacingBuilder: SpacingBuilder, private val mIndent: Boolean = false) : AbstractBlock(
    node, Wrap.createWrap(WrapType.NONE, false), alignment) {
  private fun makeChild(node: ASTNode, indent: Boolean): GnFormatBlock {
    return GnFormatBlock(node, null, mSpacingBuilder, indent)
  }

  override fun buildChildren(): List<Block> {
    val blocks: MutableList<Block> = ArrayList()
    var child = myNode.firstChildNode
    while (child != null) {
      if (child.elementType === Types.STATEMENT_LIST && myNode.elementType === Types.BLOCK
          || (child.elementType === Types.EXPR_LIST
              && myNode.elementType === Types.COLLECTION)) {
        blocks.add(makeChild(child, child.firstChildNode != null))
      } else if (child.elementType !== TokenType.WHITE_SPACE) {
        blocks.add(makeChild(child, false))
      }
      child = child.treeNext
    }
    return blocks
  }

  override fun getIndent(): Indent? {
    return if (mIndent) {
      Indent.getNormalIndent()
    } else {
      Indent.getNoneIndent()
    }
  }

  override fun getChildIndent(): Indent? {
    return if (myNode.elementType === Types.BLOCK || myNode.elementType === Types.COLLECTION) {
      Indent.getNormalIndent()
    } else Indent.getNoneIndent()
  }

  override fun getSpacing(child1: Block?, child2: Block): Spacing? {
    return mSpacingBuilder.getSpacing(this, child1, child2)
  }

  override fun isLeaf(): Boolean {
    return myNode.firstChildNode == null
  }

}
