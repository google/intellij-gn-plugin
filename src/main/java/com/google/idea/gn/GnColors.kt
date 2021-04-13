// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package com.google.idea.gn

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default

enum class GnColors(humanName: String, default: TextAttributesKey? = null) {
  BUILTIN_FUNCTION("Function", Default.PREDEFINED_SYMBOL),
  TARGET_FUNCTION("Builtin Target rule", Default.KEYWORD),
  TEMPLATE("Template call", Default.FUNCTION_CALL),
  COMMENT("Comment", Default.LINE_COMMENT),
  STRING("String", Default.STRING),
  VARIABLE("Variable", Default.INSTANCE_FIELD),
  KEYWORD("Keyword", Default.KEYWORD),
  BRACES("Braces and Operators//Braces", Default.BRACES),
  BRACKETS("Braces and Operators//Brackets", Default.BRACKETS),
  OPERATOR("Braces and Operators//Operator", Default.OPERATION_SIGN),
  PARENS("Braces and Operators//Parentheses", Default.PARENTHESES),
  DOT("Braces and Operators//Dot", Default.DOT),
  INT("Integral", Default.NUMBER);

  val textAttributesKey = TextAttributesKey.createTextAttributesKey("com.google.idea.gn.$name",
      default)
  val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
}
