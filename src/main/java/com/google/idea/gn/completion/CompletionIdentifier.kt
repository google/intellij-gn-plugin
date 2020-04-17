//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.ui.LayeredIcon
import javax.swing.Icon

interface CompletionIdentifier {
  val name: String
  val insertionText: String get() = name
  val caretShift: Int get() = 0
  val autoSuggestOnInsertion: Boolean get() = false

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
    val element = LookupElementBuilder.create(insertionText)
        .withPresentableText(name)
        .withIcon(identifierType.icon)
        .withTypeText(typeString)
        .withInsertHandler { ctx, it ->
          ctx.editor.caretModel.primaryCaret.moveCaretRelatively(caretShift, 0, false, false)
          if (autoSuggestOnInsertion) {
            AutoPopupController.getInstance(ctx.project).scheduleAutoPopup(ctx.editor)
          }
        }

    resultSet.addElement(element)
  }
}