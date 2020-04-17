//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.util.GnCodeInsightTestCase
import com.intellij.codeInsight.completion.CodeCompletionHandlerBase
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl

class AutoCompleteTest : GnCodeInsightTestCase() {

  private fun performCompletion(): List<LookupElement> {
    CodeCompletionHandlerBase(CompletionType.BASIC).invokeCompletion(project, editor)
    return LookupManager.getActiveLookup(editor)?.items ?: emptyList()
  }

  private fun performCompletionAndGetItems(): List<Pair<String, GnCompletionContributor.CompleteType?>> = performCompletion().map {
    Pair(it.lookupString, it.getUserData(GnKeys.LOOKUP_ITEM_TYPE))
  }

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
    CodeInsightTestFixtureImpl.instantiateAndRun(file, editor, IntArray(0), false)
    assertEquals(listOf(
        Pair(":g", GnCompletionContributor.CompleteType.TARGET),
        Pair("src", GnCompletionContributor.CompleteType.DIRECTORY),
        Pair("src/lib", GnCompletionContributor.CompleteType.DIRECTORY),
        Pair("src/lib:my_lib", GnCompletionContributor.CompleteType.TARGET),
        Pair("src/test", GnCompletionContributor.CompleteType.TARGET),
        Pair("src:my_src", GnCompletionContributor.CompleteType.TARGET)),
        performCompletionAndGetItems())
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
    CodeInsightTestFixtureImpl.instantiateAndRun(file, editor, IntArray(0), false)
    assertEquals(listOf(
        Pair("//:g", GnCompletionContributor.CompleteType.TARGET),
        Pair("//src", GnCompletionContributor.CompleteType.DIRECTORY),
        Pair("//src/lib", GnCompletionContributor.CompleteType.DIRECTORY),
        Pair("//src/lib:my_lib", GnCompletionContributor.CompleteType.TARGET),
        Pair("//src/test", GnCompletionContributor.CompleteType.TARGET),
        Pair("//src:my_src", GnCompletionContributor.CompleteType.TARGET)),
        performCompletionAndGetItems())
  }

  fun testImportCompletion() {
    copyTestFilesByPath {
      when (it) {
        "build/rules.gni" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """import("<caret>")""")
    CodeInsightTestFixtureImpl.instantiateAndRun(file, editor, IntArray(0), false)

    assertEquals(listOf(
        Pair("build", GnCompletionContributor.CompleteType.DIRECTORY),
        Pair("build/rules.gni", GnCompletionContributor.CompleteType.FILE)),
        performCompletionAndGetItems())
  }

  fun testAbsoluteImportCompletion() {
    copyTestFilesByPath {
      when (it) {
        "build/rules.gni" -> true
        else -> false
      }
    }
    configureFromFileText(GnFile.BUILD_FILE, """import("//<caret>")""")
    CodeInsightTestFixtureImpl.instantiateAndRun(file, editor, IntArray(0), false)

    assertEquals(listOf(
        Pair("//build", GnCompletionContributor.CompleteType.DIRECTORY),
        Pair("//build/rules.gni", GnCompletionContributor.CompleteType.FILE)),
        performCompletionAndGetItems())
  }
}