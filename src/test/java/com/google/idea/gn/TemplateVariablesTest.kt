// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.Types
import com.google.idea.gn.psi.builtin.BuiltinTargetFunction
import com.google.idea.gn.util.GnCodeInsightTestCase
import com.google.idea.gn.util.findElementMatching
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns

class TemplateVariablesTest : GnCodeInsightTestCase() {

  private fun setupAndGetFooCall(contents: String): Function {
    configureFromFileText(GnFile.BUILD_FILE, contents)
    gnFile.buildScope()
    val template = file.findElementMatching(
        psiElement(Types.CALL).withText(StandardPatterns.string().startsWith("foo")))
        ?: error(
            "Failed to find \"foo\" template call")
    return template.getUserData(GnKeys.CALL_RESOLVED_FUNCTION) ?: error(
        "resolved function not present")
  }

  fun testScopeAccess() {
    val f = setupAndGetFooCall("""
      template("foo") {
        a = invoker.apple
        b = invoker.banana
        if(invoker.orange) {
          c = invoker.pineapple
        } else if(invoker.coconut) {
          d = invoker.peach
        } else {
          f = invoker.cherry
        }
        k = invoker.plum + invoker.blueberry
      }
      
      foo("foo_target") {}
    """.trimIndent())

    assertEquals(
        setOf("apple",
            "banana",
            "orange",
            "pineapple",
            "coconut",
            "peach",
            "cherry",
            "plum",
            "blueberry"), f.variables.keys)
  }

  fun testForwardSpecificVariables() {
    val f = setupAndGetFooCall("""
      template("foo") {
        forward_variables_from(invoker, [
          "apple",
          "banana",
          "orange",
        ])
      }
      
      foo("bar") {}
    """.trimIndent())

    assertEquals(setOf("apple", "banana", "orange"), f.variables.keys)
  }

  fun testForwardIntoBuiltin() {
    val f = setupAndGetFooCall("""
      template("foo") {
        group(target_name) {
          forward_variables_from(invoker, "*")
        }
      }
      
      foo("bar") {}
    """.trimIndent())

    assertEquals(BuiltinTargetFunction.GROUP.variables, f.variables)
  }

  fun testForwardFromTemplate() {
    val f = setupAndGetFooCall("""
      template("foo") {
        template("baz") {
          a = invoker.apple
        }
        
        baz(target_name) {
          forward_variables_from(invoker, "*")
        }
      }
      
      foo("bar") {}
    """.trimIndent())

    assertEquals(setOf("apple"), f.variables.keys)
  }

  fun testForwardIntoBlock() {
    val f = setupAndGetFooCall("""
      template("foo") { 
        foo = {
          forward_variables_from(invoker, ["apple", "banana"])
        }
        forward_variables_from(invoker, ["coconut"])
      }
      
      foo("bar") {}
    """.trimIndent())

    assertEquals(setOf("apple", "banana", "coconut"), f.variables.keys)
  }

}
