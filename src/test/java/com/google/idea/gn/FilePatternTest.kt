// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilePatternTest {

  @Test
  fun testAroundWildcards() {
    val pattern = GnFilePattern("*asdf*")
    assertEquals("^.*\\Qasdf\\E.*$", pattern.regex.pattern)
    assertTrue(pattern.matches("asdf"))
    assertTrue(pattern.matches("aaaaaaaasdfbbbbb"))
    assertTrue(pattern.matches("asdfbbbb"))
    assertTrue(pattern.matches("aaaaaasdf"))
    assertFalse(pattern.matches("aaaa"))
    assertFalse(pattern.matches("sdf"))
  }

  @Test
  fun testExactMatch() {
    val pattern = GnFilePattern("asdf")
    assertEquals("^\\Qasdf\\E$", pattern.regex.pattern)
    assertTrue(pattern.matches("asdf"))
    assertFalse(pattern.matches("aaaaaaaasdfbbbbb"))
    assertFalse(pattern.matches("asdfbbbb"))
    assertFalse(pattern.matches("aaaaaasdf"))
    assertFalse(pattern.matches("aaaa"))
    assertFalse(pattern.matches("sdf"))
  }

  @Test
  fun testEndingInLiteral() {
    val pattern = GnFilePattern("*.cc")
    assertEquals("^.*\\Q.cc\\E$", pattern.regex.pattern)
    assertTrue(pattern.matches("hello.cc"))
    assertTrue(pattern.matches(".cc"))
    assertFalse(pattern.matches("hello.ccx"))
    assertFalse(pattern.matches("hellocc"))
  }

  @Test
  fun testPathBoundary() {
    val pattern = GnFilePattern("\\bwin/*")
    assertEquals("^(.*\\/)?\\Qwin/\\E.*$", pattern.regex.pattern)
    assertTrue(pattern.matches("win/foo"))
    assertTrue(pattern.matches("foo/win/bar.cc"))
    assertFalse(pattern.matches("iwin/foo"))
  }
}
