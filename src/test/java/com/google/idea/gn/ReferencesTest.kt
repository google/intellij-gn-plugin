// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.Types
import com.google.idea.gn.util.GnCodeInsightTestCase
import com.google.idea.gn.util.PatternGatherer
import com.google.idea.gn.util.findElementMatching
import com.google.idea.gn.util.resolvedReference
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.patterns.StandardPatterns

class ReferencesTest : GnCodeInsightTestCase() {

  fun testLabelReferences() {
    copyTestFiles(
        "build/rules.gni",
        "src/lib/BUILD.gn"
    )
    configureFromFileText(GnFile.BUILD_FILE, """
      import("//build/rules.gni")
      group("x") {
        deps = [
          "//src/lib:my_lib",
          "src/lib:my_lib"
        ]
      }
      
      source_set("y") {
        sources = [ "lib/lib.cc" ]
      }
    """.trimIndent())

    val elements = PatternGatherer(
        "import" to psiElement(Types.STRING_EXPR).withText("\"//build/rules.gni\""),
        "absTarget" to psiElement(Types.STRING_EXPR).withText("\"//src/lib:my_lib\""),
        "relTarget" to psiElement(Types.STRING_EXPR).withText("\"//src/lib:my_lib\""),
        "sourceFile" to psiElement(Types.STRING_EXPR).withText("\"lib/lib.cc\"")
    ).gather(file)


    assertEquals(getProjectPsiFile("build/rules.gni"),
        elements.resolvedReference("import"))
    val libGroup = getProjectPsiFile("src/lib/BUILD.gn")?.findElementMatching(
        psiElement(Types.CALL).withText(StandardPatterns.string().startsWith("group(\"my_lib\")")))
        ?: error("Failed to find group declaration in src/lib/BUILD.gn")
    assertEquals(libGroup, elements.resolvedReference("absTarget"))
    assertEquals(libGroup, elements.resolvedReference("relTarget"))
    assertEquals(getProjectPsiFile("src/lib/lib.cc"), elements.resolvedReference("sourceFile"))
  }


  fun testTemplateReferences() {
    copyTestFiles("build/rules.gni")
    configureFromFileText(GnFile.BUILD_FILE, """
      import("//build/rules.gni")
      
      template("foo") {} 
      foo("bar") {}
      test_template("baz") {}
    """.trimIndent())

    val elements = PatternGatherer(
        "foo" to psiElement(Types.ID).withText("foo"),
        "test_template" to psiElement(Types.ID).withText("test_template")
    ).gather(file)

    assertEquals(file.findElementMatching(psiElement(Types.CALL).withText(
        StandardPatterns.string().startsWith("""template("foo")"""))) ?: error(
        "Failed to match template declaration"),
        elements.resolvedReference("foo"))

    assertEquals(getProjectPsiFile("build/rules.gni")?.findElementMatching(
        psiElement(Types.CALL).withText(
            StandardPatterns.string().startsWith("""template("test_template")"""))) ?: error(
        "Failed to match template declaration"),
        elements.resolvedReference("test_template"))

  }

  fun testLabelResolvesToTemplateCall() {
    copyTestFiles("build/rules.gni")
    configureFromFileText(GnFile.BUILD_FILE, """
      import("//build/rules.gni")

      test_template("foo") {}
      
      group("test") {
        deps = [":foo"]
      }
    """.trimIndent())

    val reference = file.findElementMatching(
        psiElement(Types.STRING_EXPR).withText("\":foo\""))?.reference?.resolve()

    assertEquals(file.findElementMatching(
        psiElement(Types.CALL).withText(StandardPatterns.string().startsWith("test_template")))
        ?: error("Failed to match template call site"), reference)
  }


  fun testNestedTemplateReferences() {
    copyTestFiles("build/rules.gni")
    configureFromFileText(GnFile.BUILD_FILE, """
      template("bar") {
        group("bar" + target_name) {}
      } 
      template("foo") {
        bar("foo" + target_name) {}
      } 
      foo("baz") {}
      
      group("x") {
        deps = [":barfoobaz"]
      }
    """.trimIndent())

    if (gnFile.scope.targets?.containsKey("barfoobaz") != true) {
      error("Failed to find target in ${gnFile.scope.targets}")
    }

    val label = file.findElementMatching(psiElement(Types.STRING_EXPR).withText("""":barfoobaz""""))
        ?: error("failed to get label")

    val expect = file.findElementMatching(
        psiElement(Types.CALL).withText(
            StandardPatterns.string().startsWith("foo"))) ?: error(
        "failed to find call site")

    val ref = label.reference?.resolve()

    LOGGER.info("expect: ${expect.text}")
    LOGGER.info("got: ${ref?.text}")

    assertEquals(expect, ref)
  }

  fun testReferenceThroughTargetTemplate() {
    configureFromFileText(GnFile.BUILD_FILE, """
      template("bar") {
        target("group", target_name) {}
      }
       
      bar("baz") {}
      
      group("x") {
        deps = [":baz"]
      }
    """.trimIndent())

    if (gnFile.scope.targets?.containsKey("baz") != true) {
      error("Failed to find baz in ${gnFile.scope.targets}")
    }

    val label = file.findElementMatching(
        psiElement(Types.STRING_EXPR).withText("""":baz""""))
        ?: error("failed to get label")

    val expect = file.findElementMatching(
        psiElement(Types.CALL).withText(
            StandardPatterns.string().startsWith("bar"))) ?: error(
        "failed to find call site")

    val ref = label.reference?.resolve()

    LOGGER.info("expect: ${expect.text}")
    LOGGER.info("got: ${ref?.text}")

    assertEquals(expect, ref)
  }


}
