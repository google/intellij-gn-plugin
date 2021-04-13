// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.util.containers.toArray
import javax.swing.Icon

class GnColorSettingsPage : ColorSettingsPage {
  override fun getHighlighter(): SyntaxHighlighter = GnSyntaxHighlighter()

  override fun getIcon(): Icon? = GnIcons.FILE

  override fun getAttributeDescriptors(): Array<AttributesDescriptor> = DESCRIPTORS

  override fun getColorDescriptors(): Array<ColorDescriptor> = emptyArray()

  override fun getDisplayName(): String = "Gn"

  override fun getDemoText(): String = """
    # This is a comment
    <function>import</function>("//build.gni")
    
    <function>template</function>("my_template") {
      <var>a</var> = "value"
      <var>b</var> = {
        <var>c</var> = 1
        <var>d</var> = false
      }
      if (<var>b</var>.<var>d</var>) {
         <var>foo</var> = "bar${"$"}<var>a</var>"
      }
      <target>group</target>(<var>target_name</var>) {
        <function>forward_variables_from</function>(<var>invoker</var>, "*")
      }
    }
    
    <template>my_template</template>("hello") {
      <var>deps</var> = ["//lib:my_lib"]
    }
  """.trimIndent()

  override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = mutableMapOf(
      "function" to GnColors.BUILTIN_FUNCTION.textAttributesKey,
      "target" to GnColors.TARGET_FUNCTION.textAttributesKey,
      "template" to GnColors.TEMPLATE.textAttributesKey,
      "var" to GnColors.VARIABLE.textAttributesKey
  )

  companion object {
    val DESCRIPTORS: Array<AttributesDescriptor> = GnColors.values().sortedBy { it.name }
        .map { it.attributesDescriptor }.toArray(
            emptyArray())
  }
}
