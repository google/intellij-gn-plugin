// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.GnLanguage
import com.intellij.psi.tree.IElementType
import org.jetbrains.annotations.NonNls

class ElementType(@NonNls debugName: String) : IElementType(debugName, GnLanguage) {
      override fun toString(): String =
          when (this) {
                Types.AND_EXPR -> "&& Expr"
                Types.ARRAY_ACCESS -> "array Access"
                Types.ASSIGNMENT -> "Assignment"
                Types.ASSIGN_OP -> "Assignment Operator"
                Types.BLOCK -> "Block"
                Types.CALL -> "Call"
                Types.COLLECTION -> "Collection"
                Types.CONDITION -> "Condition"
                Types.ELSE_CONDITION -> "Else Condition"
                Types.EQUAL_EXPR -> "EQUAL_EXPR"
                Types.EXPR -> "Expression"
                Types.EXPR_LIST -> "Expression-List"
                Types.GE_EXPR -> "> Expr"
                Types.GT_EXPR -> ">= Expr"
                Types.ID -> "Identifier"
                Types.LE_EXPR -> "<= Expr"
                Types.LITERAL_EXPR -> "Literal Expr"
                Types.LT_EXPR -> "< Expr"
                Types.LVALUE -> "LValue"
                Types.MINUS_EXPR -> "- Expr"
                Types.NOT_EQUAL_EXPR -> "!= Expr"
                Types.OR_EXPR -> "|| Expr"
                Types.PAREN_EXPR -> "( Expr )"
                Types.PLUS_EXPR -> "+ Expr"
                Types.PRIMARY_EXPR -> "Primary-Expr"
                Types.SCOPE_ACCESS -> ". Scope Access"
                Types.STATEMENT -> "Statement"
                Types.STATEMENT_LIST -> "Statement-List"
                Types.UNARY_EXPR -> "Unary-Expr"
                Types.STRING_EXPAND -> "String expansion"
                Types.STRING_HEX -> "String hex expansion"
        Types.STRING_EXPR -> "String expression"
        Types.STRING_LITERAL_EXPR -> "String literal expresion"
        Types.STRING_IDENT -> "String identifier access"
        Types.STRING_INNER -> "String contents"
        else -> super.toString()
      }
}
