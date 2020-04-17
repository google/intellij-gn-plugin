// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.psi.scope.Scope
import com.google.idea.gn.util.GnCodeInsightTestCase


class ExprTest : GnCodeInsightTestCase() {

  companion object {
    const val TEST_VAR = "my_var"

    // Better dollar in char strings.
    const val DS = "$"
  }

  private fun executeFile(text: String, inject: Map<String, GnValue>? = null): Scope {
    configureFromFileText("expr_test.gn", text)

    when (val file = this.file) {
      is GnFile -> return file.buildScope(inject)
      else -> {
        kotlin.test.fail("$file is not GnFile")
      }
    }
  }

  private fun executeAndGetTestVar(text: String, inject: Map<String, GnValue>? = null): GnValue {
    val scope = executeFile(text, inject)
    val result = scope.getVariable(TEST_VAR)
    kotlin.test.assertNotNull(result)
    val value = result.value
    kotlin.test.assertNotNull(value)
    return value
  }

  fun testListDeclaration() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = ["a", "b", "c"]
    """)
    assertEquals(listOf(GnValue("a"), GnValue("b"), GnValue("c")), result.value)
  }

  fun testBlockDeclaration() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = { 
        a = 2 
        b = "bee"
      }
    """)
    assertEquals(mapOf("a" to GnValue(2), "b" to GnValue("bee")), result.value)
  }

  fun testStringEval() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = "abc"
    """)
    assertEquals("abc", result.value)
  }

  fun testStringIdentExpand() {
    val result = executeAndGetTestVar("""
      my_id = "012"
      $TEST_VAR = "abc${DS}my_id"
    """)
    assertEquals("abc012", result.value)
  }

  fun testStringIdentExprExpand() {
    val result = executeAndGetTestVar("""
      my_id = "012"
      $TEST_VAR = "abc${DS}{my_id}"
    """)
    assertEquals("abc012", result.value)
  }


  fun testStringScopeAccessExpand() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = "012${DS}{my_scope.my_inner_var}012"
    """, mapOf("my_scope" to GnValue(mapOf("my_inner_var" to GnValue("abc")))))
    assertEquals("012abc012", result.value)
  }

  fun testStringHexExpand() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = "abc$0x39$0x5A"
    """)
    assertEquals("abc9Z", result.value)
  }

  fun testEmptyScope() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = {}
    """)
    assertEquals(GnValue(emptyMap()), result)
  }

  fun testScopeAccess() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = my_scope.my_inner_var
    """, inject = mapOf("my_scope" to GnValue(mapOf("my_inner_var" to GnValue("abc")))))
    assertEquals("abc", result.value)
  }

  fun testIntegerLiterals() {
    val scope = executeFile("""
      positive_int = 3
      negative_int = -7
    """)
    assertEquals(3, scope.getVariable("positive_int")?.value?.value)
    assertEquals(-7, scope.getVariable("negative_int")?.value?.value)
  }

  fun testArrayAccess() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = my_list[1]
    """, inject = mapOf("my_list" to GnValue(listOf(GnValue("bad"), GnValue("good")))))
    assertEquals("good", result.value)
  }

  fun testBooleans() {
    val scope = executeFile("""
      true_var = true
      false_var = false
    """)
    assertEquals(true, scope.getVariable("true_var")?.value?.value)
    assertEquals(false, scope.getVariable("false_var")?.value?.value)
  }

  fun testUnaryNot() {
    val result = executeAndGetTestVar("""
      other = true
      $TEST_VAR = !other
    """)
    assertEquals(false, result.value)
  }

  fun testGreaterThan() {
    val scope = executeFile("""
      true_result = 3 > 2
      false_result = 2 > 3
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testGreaterThanOrEqual() {
    val scope = executeFile("""
      true_result = 3 >= 2
      false_result = 2 >= 3
      true_equal_result = 3 >= 3
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(true, scope.getVariable("true_equal_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testLessThan() {
    val scope = executeFile("""
      true_result = 2 < 3
      false_result = 3 < 2
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testLessThanOrEqual() {
    val scope = executeFile("""
      true_result = 2 <= 3
      false_result = 3 <= 2
      true_equal_result = 3 <= 3
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(true, scope.getVariable("true_equal_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testAnd() {
    val scope = executeFile("""
      true_result = true && true
      false_result = true && false
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testOr() {
    val scope = executeFile("""
      true_result = true || false
      false_result = false || false
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testEqualString() {
    val scope = executeFile("""
      true_result = "abc" == "abc"
      false_result = "abc" == "def"
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testEqualBool() {
    val scope = executeFile("""
      true_result = false == false 
      false_result = false == true
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testEqualInt() {
    val scope = executeFile("""
      true_result = 3 == 3 
      false_result = 2 == 3
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testEqualList() {
    val scope = executeFile("""
      true_result = ["a", "b", "c"] == ["a", "b", "c"] 
      false_result = ["a", "b", "c"] == ["b", "a", "c"] 
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testEqualScope() {
    val scope = executeFile("""
      scope_a = { a = "a" b = "b" }
      scope_b = { a = "a" b = "b" }
      scope_c = { a = "a" b = "f" }
      true_result = scope_a == scope_b 
      false_result = scope_a == scope_c 
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testNotEqual() {
    val scope = executeFile("""
      true_result = 2 != 3 
      false_result = 3 != 3
    """)
    assertEquals(true, scope.getVariable("true_result")?.value?.value)
    assertEquals(false, scope.getVariable("false_result")?.value?.value)
  }

  fun testPlusString() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = "abc" + "def"
    """)
    assertEquals("abcdef", result.value)
  }

  fun testPlusInt() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = 2 + 3
    """)
    assertEquals(5, result.value)
  }

  fun testPlusList() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = ["a", "b"] + [1, 2]
    """)
    assertEquals(listOf(GnValue("a"), GnValue("b"), GnValue(1), GnValue(2)), result.value)
  }

  fun testMinusInt() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = 3 - 2
    """)
    assertEquals(1, result.value)
  }

  fun testMinusList() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = ["a", "a", "b", "c", "e", "e"] - ["a", "e"]
    """)
    assertEquals(listOf("b", "c").map { GnValue(it) }, result.value)
  }

  fun testIf() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = "not_ok"
      if(true) {
        $TEST_VAR = "ok"
      }
    """)
    assertEquals("ok", result.value)
  }

  fun testElse() {
    val result = executeAndGetTestVar("""
      if(false) {
        $TEST_VAR = "not_ok"
      } else {
        $TEST_VAR = "ok"
      }
    """)
    assertEquals("ok", result.value)
  }

  fun testElseIf() {
    val result = executeAndGetTestVar("""
      if(false) {
        $TEST_VAR = "not_ok"
      } else if(true) {
        $TEST_VAR = "ok"
      } else {
        $TEST_VAR = "not_ok_else"
      }
    """)
    assertEquals("ok", result.value)
  }

  fun testPlusEqualString() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = "abc"
      $TEST_VAR += "def"
    """)
    assertEquals("abcdef", result.value)
  }

  fun testPlusEqualInt() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = 2
      $TEST_VAR += 3
    """)
    assertEquals(5, result.value)
  }

  fun testPlusEqualList() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = ["a", "b"]
      $TEST_VAR += "c"
      $TEST_VAR += ["d", "e"]
    """)
    assertEquals(listOf("a", "b", "c", "d", "e").map { GnValue(it) }, result.value)
  }

  fun testMinusEqualInt() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = 2
      $TEST_VAR -= 3
    """)
    assertEquals(-1, result.value)
  }

  fun testMinusEqualList() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = ["c", "a", "b", "e", "d", "c", "e"]
      $TEST_VAR -= "c"
      $TEST_VAR -= ["d", "e"]
    """)
    assertEquals(listOf("a", "b").map { GnValue(it) }, result.value)
  }

  fun testArrayLValue() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = ["a", "b"]
      $TEST_VAR[1] = "x"
      $TEST_VAR[0] += "b"
    """)
    assertEquals(listOf("ab", "x").map { GnValue(it) }, result.value)
  }

  fun testScopeLValue() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = {
        a = "x"
        b = "y"
      }
      ${TEST_VAR}.a = "z"
      ${TEST_VAR}.b += "z"
      ${TEST_VAR}.c = "cee"
    """)
    assertEquals(mapOf("a" to "z", "b" to "yz", "c" to "cee").mapValues { GnValue(it.value) },
        result.value)
  }

  fun testOperatorPrecedence() {
    val result = executeAndGetTestVar("""
      $TEST_VAR = 2 + 3 - 4 == 1 && 2 + 3 > 1 + 1 || 3 + 2 != 1 + 2"
    """)
    assertEquals(true, result.value)
  }


}
