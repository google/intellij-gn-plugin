//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.GnLabel.Companion.parse
import org.junit.Assert
import org.junit.Test

class GnLabelTest {
  @Test
  fun completeAbsolutePath() {
    val path = parse("//src/lib/my_lib:my_target(my_toolchain)")
    Assert.assertNotNull(path)
    Assert.assertTrue(path!!.isAbsolute)
    Assert.assertEquals(3, path.parts.size)
    Assert.assertEquals("src", path.parts[0])
    Assert.assertEquals("lib", path.parts[1])
    Assert.assertEquals("my_lib", path.parts[2])
    Assert.assertEquals("my_target", path.target)
    Assert.assertEquals("my_toolchain", path.toolchain)
    Assert.assertEquals("//src/lib/my_lib:my_target(my_toolchain)", path.toString())
  }

  @Test
  fun basicAbsolutePath() {
    val path = parse("//src/lib/my_lib")
    Assert.assertNotNull(path)
    Assert.assertTrue(path!!.isAbsolute)
    Assert.assertEquals(3, path.parts.size)
    Assert.assertEquals("src", path.parts[0])
    Assert.assertEquals("lib", path.parts[1])
    Assert.assertEquals("my_lib", path.parts[2])
    Assert.assertEquals("my_lib", path.target)
    Assert.assertNull(path.toolchain)
    Assert.assertEquals("//src/lib/my_lib", path.toString())
  }

  @Test
  fun variablesInPath() {
    val path = parse("//src/lib/my_lib:target(toolchain)")
    Assert.assertNotNull(path)
    Assert.assertTrue(path!!.isAbsolute)
    Assert.assertEquals(3, path.parts.size)
    Assert.assertEquals("src", path.parts[0])
    Assert.assertEquals("lib", path.parts[1])
    Assert.assertEquals("target", path.target)
    Assert.assertEquals("toolchain", path.toolchain)
    Assert.assertEquals("//src/lib/my_lib:target(toolchain)", path.toString())
  }

  @Test
  fun relativePath() {
    val path = parse("lib/my_lib")
    Assert.assertNotNull(path)
    Assert.assertFalse(path!!.isAbsolute)
    Assert.assertEquals(2, path.parts.size)
    Assert.assertEquals("lib", path.parts[0])
    Assert.assertEquals("my_lib", path.parts[1])
    Assert.assertEquals("my_lib", path.target)
    Assert.assertNull(path.toolchain)
    Assert.assertEquals("lib/my_lib", path.toString())
  }

  @Test
  fun allowIncompleteTarget() {
    val path = parse("lib:")
    Assert.assertNotNull(path)
    Assert.assertEquals("", path!!.target)
  }

  @Test
  fun targetInFilePath() {
    val path = parse(":target")
    Assert.assertNotNull(path)
    Assert.assertEquals(0, path!!.parts.size)
    Assert.assertEquals("target", path.target)
    Assert.assertNull(path.toolchain)
    Assert.assertEquals(":target", path.toString())
  }

  @Test
  fun invalidPath() {
    Assert.assertNull(parse(""))
    Assert.assertNull(parse("//"))
    Assert.assertNull(parse(null))
  }
}