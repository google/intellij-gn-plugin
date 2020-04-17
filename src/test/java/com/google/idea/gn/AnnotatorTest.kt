//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.util.GnCodeInsightTestCase
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl

class AnnotatorTest : GnCodeInsightTestCase() {

  class HighlightChecker(val text: String, val highlightKey: TextAttributesKey) {

    constructor(text: String, color: GnColors) : this(text, color.textAttributesKey)

    constructor(info: HighlightInfo) : this(info.text, info.forcedTextAttributesKey)

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is HighlightChecker) return false

      if (text != other.text) return false
      if (highlightKey != other.highlightKey) return false

      return true
    }

    override fun toString(): String {
      return "HighlightChecker(text='$text', highlightKey='$highlightKey')"
    }


  }

  fun testColorAnnotations() {
    configureFromFileText(GnFile.BUILD_FILE, """
    import("//build.gni")
    
    template("my_template") {
      a = { b = true }
      a.b = false
      group(target_name) {
        forward_variables_from(invoker, "*")
      }
    }
    """.trimIndent())
    val highlight = CodeInsightTestFixtureImpl.instantiateAndRun(file, editor, IntArray(0), false)
    assertEquals(listOf(
        HighlightChecker("import", GnColors.BUILTIN_FUNCTION),
        HighlightChecker("template", GnColors.BUILTIN_FUNCTION),
        HighlightChecker("a", GnColors.VARIABLE),
        HighlightChecker("b", GnColors.VARIABLE),
        HighlightChecker("a", GnColors.VARIABLE),
        HighlightChecker("b", GnColors.VARIABLE),
        HighlightChecker("group", GnColors.TARGET_FUNCTION),
        HighlightChecker("target_name", GnColors.VARIABLE),
        HighlightChecker("invoker", GnColors.VARIABLE)
    ), highlight.map { HighlightChecker(it) })
  }

}