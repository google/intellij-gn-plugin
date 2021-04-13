// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.intellij.testFramework.ParsingTestCase

class ParseTest : ParsingTestCase("", "gn", true, GnParserDefinition()) {

  override fun getTestDataPath(): String = "src/test/testData"

  override fun skipSpaces(): Boolean = true

  override fun includeRanges(): Boolean = true

  fun testSimpleParseCheck() {
    doTest(true)
  }

}
