// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.GnLabel.Companion.parse
import org.junit.Test
import kotlin.test.*

class GnLabelTest {

  @Test
  fun testCompleteAbsolutePath() {
    val path = parse("//src/lib/my_lib:my_target(my_toolchain)")
    assertNotNull(path)
    assertTrue(path.isAbsolute)
    assertEquals(3, path.parts.size)
    assertEquals("src", path.parts[0])
    assertEquals("lib", path.parts[1])
    assertEquals("my_lib", path.parts[2])
    assertEquals("my_target", path.target)
    assertEquals("my_toolchain", path.toolchain)
    assertEquals("//src/lib/my_lib:my_target(my_toolchain)", path.toString())
  }


  @Test
  fun testBasicAbsolutePath() {
    val path = parse("//src/lib/my_lib")
    assertNotNull(path)
    assertTrue(path.isAbsolute)
    assertEquals(3, path.parts.size)
    assertEquals("src", path.parts[0])
    assertEquals("lib", path.parts[1])
    assertEquals("my_lib", path.parts[2])
    assertEquals("my_lib", path.target)
    assertNull(path.toolchain)
    assertEquals("//src/lib/my_lib", path.toString())
  }


  @Test
  fun testVariablesInPath() {
    val path = parse("//src/lib/my_lib:target(toolchain)")
    assertNotNull(path)
    assertTrue(path.isAbsolute)
    assertEquals(3, path.parts.size)
    assertEquals("src", path.parts[0])
    assertEquals("lib", path.parts[1])
    assertEquals("target", path.target)
    assertEquals("toolchain", path.toolchain)
    assertEquals("//src/lib/my_lib:target(toolchain)", path.toString())
  }

  @Test
  fun testRelativePath() {
    val path = parse("lib/my_lib")
    assertNotNull(path)
    assertFalse(path.isAbsolute)
    assertEquals(2, path.parts.size)
    assertEquals("lib", path.parts[0])
    assertEquals("my_lib", path.parts[1])
    assertEquals("my_lib", path.target)
    assertNull(path.toolchain)
    assertEquals("lib/my_lib", path.toString())
  }

  @Test
  fun testAllowIncompleteTarget() {
    val path = parse("lib:")
    assertNotNull(path)
    assertEquals("", path.target)
  }

  @Test
  fun testTargetInFilePath() {
    val path = parse(":target")
    assertNotNull(path)
    assertEquals(0, path.parts.size)
    assertEquals("target", path.target)
    assertNull(path.toolchain)
    assertEquals(":target", path.toString())
  }

  @Test
  fun testInvalidPath() {
    assertNull(parse(""))
    assertNull(parse("//"))
    assertNull(parse(null))
  }
}
