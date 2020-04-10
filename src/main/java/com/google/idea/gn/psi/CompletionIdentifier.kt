//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn.psi

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder

interface CompletionIdentifier {
  val name: String
  val insertionText: String get() = name
  val caretShift: Int get() = 0
  val autoSuggestOnInsertion: Boolean get() = false

  fun gatherChildren(operator: (CompletionIdentifier) -> Unit) = Unit

  val isCall: Boolean get() = false

  fun addToResult(resultSet: CompletionResultSet) {

    val element = LookupElementBuilder.create(insertionText)
        .withPresentableText(name)
        .withInsertHandler { ctx, it ->
          ctx.editor.caretModel.primaryCaret.moveCaretRelatively(caretShift, 0, false, false)
          if(autoSuggestOnInsertion) {
            AutoPopupController.getInstance(ctx.project).scheduleAutoPopup(ctx.editor)
          }
        }

    resultSet.addElement(element)
  }
}