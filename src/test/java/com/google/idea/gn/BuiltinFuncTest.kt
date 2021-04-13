// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.util.GnCodeInsightTestCase

class BuiltinFuncTest : GnCodeInsightTestCase() {

  fun testForwardVariablesFrom() {
    configureFromFileText(GnFile.BUILD_FILE, """
      a = {
        a1 = 1
        a2 = 2
        a3 = 3
      }
      
      b = {
        forward_variables_from(a, "*", ["a2"])
      }
      
      c = {
        forward_variables_from(a, ["a1", "a3"])
      }
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error("Failed to get variables")

    val expect = GnValue(mapOf("a1" to GnValue(1),
        "a3" to GnValue(3)))

    assertEquals(expect, vars["b"])
    assertEquals(expect, vars["c"])
  }

  fun testDeclareArgs() {
    configureFromFileText(GnFile.BUILD_FILE, """
     declare_args() {
       a = "a"
       b = "b"
     }
    """.trimIndent())

    val vars = gnFile.scope.consolidateVariables() ?: error("Failed to get variables")

    assertEquals(
        mapOf("a" to GnValue("a"), "b" to GnValue("b")),
        vars)
  }

  fun testDefined() {
    configureFromFileText(GnFile.BUILD_FILE, """
     foo = "1"
     bar = {
      baz = "2"
     }
     a = defined(foo)
     b = defined(zzz)
     c = defined(bar.baz)
     d = defined(bar.foo)
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error("Failed to get variables")

    assertEquals(GnValue(true), vars["a"])
    assertEquals(GnValue(false), vars["b"])
    assertEquals(GnValue(true), vars["c"])
    assertEquals(GnValue(false), vars["d"])
  }

  fun testFilterExclude() {
    configureFromFileText(GnFile.BUILD_FILE, """
     a = filter_exclude(["aaa", "bbb", "ccb"], ["*b"])
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error("Failed to get variables")
    assertEquals(GnValue(listOf(GnValue("aaa"))), vars["a"])
  }

  fun testFilterInclude() {
    configureFromFileText(GnFile.BUILD_FILE, """
     a = filter_include(["aaa", "bbb", "ccb"], ["*b"])
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error("Failed to get variables")
    assertEquals(GnValue(listOf(GnValue("bbb"), GnValue("ccb"))), vars["a"])
  }

  fun testForeach() {
    configureFromFileText(GnFile.BUILD_FILE, """
      i = "foo"
      z = []
      foreach(i, ["a", "b", "c"]) {
        z += i
      }
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error("Failed to get variables")
    assertEquals(GnValue("foo"), vars["i"])
    assertEquals(GnValue(listOf("a", "b", "c").map { GnValue(it) }), vars["z"])
  }

  fun testGetLabelInfo() {
    configureFromFileText("foo/${GnFile.BUILD_FILE}", """
      label = "lib:bar(//toolchain)"
      l_name = get_label_info(label, "name")
      l_dir = get_label_info(label, "dir")
      l_target_gen_dir = get_label_info(label, "target_gen_dir")
      l_root_gen_dir = get_label_info(label, "root_gen_dir")
      l_target_out_dir = get_label_info(label, "target_out_dir")
      l_root_out_dir = get_label_info(label, "root_out_dir")
      l_label_no_toolchain = get_label_info(label, "label_no_toolchain")
      l_label_with_toolchain = get_label_info(label, "label_with_toolchain")
      l_toolchain = get_label_info(label, "toolchain")
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables()?.mapValues { it.value.string } ?: error(
        "Failed to get variables")
    assertEquals("bar", vars["l_name"])
    assertEquals("//foo/lib", vars["l_dir"])
    assertEquals(null, vars["l_target_gen_dir"])
    assertEquals(null, vars["l_root_gen_dir"])
    assertEquals(null, vars["l_target_out_dir"])
    assertEquals(null, vars["l_root_out_dir"])
    assertEquals("//foo/lib:bar", vars["l_label_no_toolchain"])
    assertEquals("//foo/lib:bar(//toolchain)", vars["l_label_with_toolchain"])
    assertEquals("//toolchain", vars["l_toolchain"])
  }

  fun testGetPathInfo() {
    configureFromFileText("foo/${GnFile.BUILD_FILE}", """
      path = "foo/bar.txt"
      l_file = get_path_info(path, "file")
      l_name = get_path_info(path, "name")
      l_extension = get_path_info(path, "extension")
      l_dir = get_path_info(path, "dir")
      l_out_dir = get_path_info(path, "out_dir")
      l_gen_dir = get_path_info(path, "gen_dir")
      l_abspath = get_path_info(path, "abspath")
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables()?.mapValues { it.value.string } ?: error(
        "Failed to get variables")
    assertEquals("bar.txt", vars["l_file"])
    assertEquals("bar", vars["l_name"])
    assertEquals("txt", vars["l_extension"])
    assertEquals("foo", vars["l_dir"])
    assertEquals(null, vars["l_out_dir"])
    assertEquals(null, vars["l_gen_dir"])
    assertEquals(null, vars["l_abspath"])
  }

  fun testProcessFileTemplate() {
    // NOTE process_file_template is not currently fully implemented, we're only testing
    // the mock version here.

    configureFromFileText(GnFile.BUILD_FILE, """
      a = process_file_template(["foo.txt", "bar.txt"], ["//{bla}", "{bla}/x"])
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error(
        "Failed to get variables")
    assertEquals(
        GnValue(listOf("//foo.txt", "foo.txt/x", "//bar.txt", "bar.txt/x").map { GnValue(it) }),
        vars["a"])
  }

  fun testRebasePath() {
    // NOTE rebase_path is not fully implemented, we're only testing
    // the mock version here.

    configureFromFileText(GnFile.BUILD_FILE, """
      a = rebase_path("foo/bar")
      b = rebase_path(["foo", "bar"])
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error(
        "Failed to get variables")
    assertEquals(
        GnValue("foo/bar"),
        vars["a"])
    assertEquals(
        GnValue(listOf("foo", "bar").map { GnValue(it) }),
        vars["b"])
  }

  fun testSplitList() {
    configureFromFileText(GnFile.BUILD_FILE, """
      list = [0,1,2]
      a = split_list(list, 3)
      b = split_list(list, 2)
      c = split_list(list, 4)
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error(
        "Failed to get variables")
    assertEquals(
        GnValue(listOf(0, 1, 2).map { GnValue(listOf(GnValue(it))) }),
        vars["a"])
    assertEquals(
        GnValue(
            listOf(
                GnValue(listOf(GnValue(0), GnValue(1))),
                GnValue(listOf(GnValue(2)))
            )
        ),
        vars["b"])
    assertEquals(
        GnValue(
            listOf(
                GnValue(listOf(GnValue(0))),
                GnValue(listOf(GnValue(1))),
                GnValue(listOf(GnValue(2))),
                GnValue(listOf())
            )
        ),
        vars["c"])
  }

  fun testStringJoin() {
    configureFromFileText(GnFile.BUILD_FILE, """
      a = string_join(", ", ["a", "b", "c"])
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error(
        "Failed to get variables")
    assertEquals(GnValue("a, b, c"), vars["a"])
  }

  fun testStringReplace() {
    configureFromFileText(GnFile.BUILD_FILE, """
      a = string_replace("hello world", "world", "GN")
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error(
        "Failed to get variables")
    assertEquals(GnValue("hello GN"), vars["a"])
  }

  fun testStringSplit() {
    configureFromFileText(GnFile.BUILD_FILE, """
      a = string_split("hello world")
    """.trimIndent())
    val vars = gnFile.scope.consolidateVariables() ?: error(
        "Failed to get variables")
    assertEquals(GnValue(listOf("hello", "world").map { GnValue(it) }), vars["a"])
  }

}
