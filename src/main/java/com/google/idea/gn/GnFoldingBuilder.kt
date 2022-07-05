package com.google.idea.gn

import com.google.idea.gn.psi.impl.GnBlockImpl
import com.google.idea.gn.psi.impl.GnCollectionImpl
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.util.*

class GnFoldingBuilder : FoldingBuilderEx(), DumbAware {
    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors: MutableList<FoldingDescriptor> = ArrayList()

        //Block
        val blocks = PsiTreeUtil.findChildrenOfType(root, GnBlockImpl::class.java)
        for (block in blocks) {
            val first = block.firstChild
            val last = block.lastChild
            val newRange = TextRange(first.textRange.endOffset, last.textRange.startOffset)
            if (newRange.length > 0) {
                descriptors.add(FoldingDescriptor(block, newRange))
            }
        }

        // Collections
        val collections = PsiTreeUtil.findChildrenOfType(root, GnCollectionImpl::class.java)
        for (collection in collections) {
            val first = collection.firstChild
            val last = collection.lastChild
            val newRange = TextRange(first.textRange.endOffset, last.textRange.startOffset)
            if (newRange.length > 0) {
                descriptors.add(FoldingDescriptor(collection, newRange))
            }
        }
        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode): String? {
        return "..."
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        return false
    }
}
