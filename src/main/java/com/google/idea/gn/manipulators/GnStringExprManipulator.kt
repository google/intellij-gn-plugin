package com.google.idea.gn.manipulators

import com.google.idea.gn.psi.impl.GnStringExprImpl
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator


class GnStringExprManipulator : AbstractElementManipulator<GnStringExprImpl>() {
    override fun handleContentChange(
        element: GnStringExprImpl,
        range: TextRange,
        newContent: String?
    ): GnStringExprImpl? {
        // TODO: Implement this
        LOG.warn("Renaming GN Expression is currently not supported")
        return null
    }

    companion object {
        private val LOG = logger<GnStringExprManipulator>()
    }
}
