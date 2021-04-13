// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn.completion

import com.google.idea.gn.GnKeys
import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.ui.LayeredIcon
import javax.swing.Icon

interface CompletionIdentifier {
  val identifierName: String
  val autoSuggestOnInsertion: Boolean get() = false
  val postInsertType: PostInsertType? get() = null


  enum class PostInsertType(val extra: String, val caretShift: Int) {
    CALL("()", 1),
    CALL_WITH_STRING("(\"\")", 2);
  }

  enum class IdentifierType {
    FUNCTION,
    TARGET_FUNCTION,
    VARIABLE,
    FUNCTION_VARIABLE,
    TEMPLATE;

    val icon: Icon
      get() = when (this) {
        FUNCTION -> AllIcons.Nodes.Function
        TARGET_FUNCTION -> LayeredIcon.create(AllIcons.Nodes.Target, AllIcons.Nodes.Shared)
        VARIABLE -> AllIcons.Nodes.Variable
        TEMPLATE -> AllIcons.Actions.Lightning
        FUNCTION_VARIABLE -> LayeredIcon.create(AllIcons.Nodes.Variable, AllIcons.Nodes.StaticMark)
      }
  }

  val identifierType: IdentifierType

  val typeString: String? get() = null

  fun gatherChildren(operator: (CompletionIdentifier) -> Unit) = Unit

  fun addToResult(resultSet: CompletionResultSet) {
    resultSet.startBatch()
    val element = LookupElementBuilder.create(identifierName)
        .withIcon(identifierType.icon)
        .withTypeText(typeString)
        .withInsertHandler { ctx, _ ->
          postInsertType?.let {
            ctx.document.insertString(ctx.tailOffset, it.extra)
            ctx.editor.caretModel.primaryCaret.moveCaretRelatively(it.caretShift, 0, false, false)
          }
          if (autoSuggestOnInsertion) {
            AutoPopupController.getInstance(ctx.project).scheduleAutoPopup(ctx.editor)
          }
        }
    element.putUserData(GnKeys.IDENTIFIER_COMPLETION_TYPE, identifierType)
    resultSet.addElement(element)
  }
}
