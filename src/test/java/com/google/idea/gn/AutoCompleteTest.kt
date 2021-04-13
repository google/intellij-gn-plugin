// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.completion.FileCompletionProvider
import com.google.idea.gn.psi.Builtin
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.builtin.BuiltinTargetFunction
import com.google.idea.gn.util.GnCodeInsightTestCase
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.Lookup
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.util.Key

class AutoCompleteTest : GnCodeInsightTestCase() {

  private fun performCompletion(): List<LookupElement> {
    CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor)
    return LookupManager.getActiveLookup(editor)?.items ?: emptyList()
  }

  private fun performLabelCompletion() = performCompletionAndGetItemsWithKey(
      GnKeys.LABEL_COMPLETION_TYPE)

  private fun performIdentifierCompletion() = performCompletionAndGetItemsWithKey(
      GnKeys.IDENTIFIER_COMPLETION_TYPE)

  private fun <T> performCompletionAndGetItemsWithKey(key: Key<T>): List<Pair<String, T?>> =
      performCompletion().map { it.lookupString to it.getUserData(key) }

  fun testLabelCompletion() {
    copyTestFilesByPath {
      when (it) {
        "BUILD.gn",
        "src/BUILD.gn",
        "src/lib/BUILD.gn",
        "src/test/BUILD.gn" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """group("g"){deps=["<caret>"]}""")

    assertEquals(listOf(
        Pair(":g", FileCompletionProvider.CompleteType.TARGET),
        Pair("src", FileCompletionProvider.CompleteType.DIRECTORY),
        Pair("src/lib", FileCompletionProvider.CompleteType.DIRECTORY),
        Pair("src/lib:my_lib", FileCompletionProvider.CompleteType.TARGET),
        Pair("src/test", FileCompletionProvider.CompleteType.TARGET),
        Pair("src:my_src", FileCompletionProvider.CompleteType.TARGET)),
        performLabelCompletion())
  }

  fun testAbsoluteLabelCompletion() {
    copyTestFilesByPath {
      when (it) {
        "BUILD.gn",
        "src/BUILD.gn",
        "src/lib/BUILD.gn",
        "src/test/BUILD.gn" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """group("g"){deps=["//<caret>"]}""")

    assertEquals(listOf(
        Pair("//:g", FileCompletionProvider.CompleteType.TARGET),
        Pair("//src", FileCompletionProvider.CompleteType.DIRECTORY),
        Pair("//src/lib", FileCompletionProvider.CompleteType.DIRECTORY),
        Pair("//src/lib:my_lib", FileCompletionProvider.CompleteType.TARGET),
        Pair("//src/test", FileCompletionProvider.CompleteType.TARGET),
        Pair("//src:my_src", FileCompletionProvider.CompleteType.TARGET)),
        performLabelCompletion())
  }

  fun testImportCompletion() {
    copyTestFilesByPath {
      when (it) {
        "build/rules.gni" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """import("<caret>")""")

    assertEquals(listOf(
        Pair("build", FileCompletionProvider.CompleteType.DIRECTORY),
        Pair("build/rules.gni", FileCompletionProvider.CompleteType.FILE)),
        performLabelCompletion())
  }

  fun testAbsoluteImportCompletion() {
    copyTestFilesByPath {
      when (it) {
        "build/rules.gni" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """import("//<caret>")""")

    assertEquals(listOf(
        Pair("//build", FileCompletionProvider.CompleteType.DIRECTORY),
        Pair("//build/rules.gni", FileCompletionProvider.CompleteType.FILE)),
        performLabelCompletion())
  }

  fun testIdentifierCompletionWithinTargetCall() {
    configureFromFileText(GnFile.BUILD_FILE, """
      global = "x"
      group("g") {
        local = "d"
        <caret>
      }
    """.trimIndent())

    val expect = setOf(
        "global" to CompletionIdentifier.IdentifierType.VARIABLE,
        "local" to CompletionIdentifier.IdentifierType.VARIABLE)
        .plus(Builtin.FUNCTIONS.values.filter { it !is BuiltinTargetFunction }
            .map { it.identifierName to it.identifierType })
        .plus(
            BuiltinTargetFunction.GROUP.variables.values.map { it.identifierName to it.identifierType })

    assertEquals(expect,
        performIdentifierCompletion().toSet())
  }

  fun testIdentifierCompletionFromFileRoot() {
    configureFromFileText(GnFile.BUILD_FILE, """
      global = "x"
      
      template("my_template") {
      }
      
      <caret>
    """.trimIndent())
    val expect = setOf(
        "global" to CompletionIdentifier.IdentifierType.VARIABLE,
        "my_template" to CompletionIdentifier.IdentifierType.TEMPLATE)
        .plus(Builtin.FUNCTIONS.values.map { it.identifierName to it.identifierType })
    assertEquals(expect, performIdentifierCompletion().toSet())
  }

  fun testIdentifierExtraInsertion() {
    configureFromFileText(GnFile.BUILD_FILE, "templ<caret>")
    val item = performCompletion()[0]
    runWriteAction {
      editor.document.replaceString(0, editor.caretModel.primaryCaret.offset, "")
      CompletionUtil.emulateInsertion(item, editor.caretModel.primaryCaret.offset,
          InsertionContext(OffsetMap(editor.document), Lookup.AUTO_INSERT_SELECT_CHAR,
              arrayOf(item),
              file, editor, false))
    }
    assertEquals("""template("")""", editor.document.text)
    assertEquals("template(\"".length, editor.caretModel.primaryCaret.offset)
  }
}
