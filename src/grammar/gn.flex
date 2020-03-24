//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.

package com.google.idea.gn;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import static com.google.idea.gn.psi.Types.*;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

%%

%class GnLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

WHITE_SPACE=\s+

// Reserved words
IF="if"
ELSE="else"

// Operators
OBRACE="{"
CBRACE="}"
DOT="."
COMMA=","
OPAREN="("
CPAREN=")"
OBRACKET="["
CBRACKET="]"
EQUAL_EQUAL="=="
PLUS_EQUAL="+="
MINUS_EQUAL="-="
LESSER_EQUAL="<="
GREATER_EQUAL=">="
NOT_EQUAL="!="
EQUAL="="
UNARY_NOT="!"
PLUS="+"
MINUS="-"
LESSER="<"
GREATER=">"
AND="&&"
OR="||"
QUOTE=\"

// Literals
TRUE="true"
FALSE="false"
STRING_LITERAL=\"([^\"\\]|\\.)*\"*
INTEGRAL_LITERAL=[+-]?[0-9]+

IDENTIFIER=[:letter:][a-zA-Z_0-9]*
COMMENT="#".*

%%

<YYINITIAL> {
  {WHITE_SPACE} { return WHITE_SPACE; }
  {IF} { return IF; }
  {ELSE} { return ELSE; }
  {OBRACE} { return OBRACE; }
  {CBRACE} { return CBRACE; }
  {DOT} { return DOT; }
  {COMMA} { return COMMA; }
  {OPAREN} { return OPAREN; }
  {CPAREN} { return CPAREN; }
  {OBRACKET} { return OBRACKET; }
  {CBRACKET} { return CBRACKET; }
  {EQUAL_EQUAL} { return EQUAL_EQUAL; }
  {PLUS_EQUAL} { return PLUS_EQUAL; }
  {MINUS_EQUAL} { return MINUS_EQUAL; }
  {LESSER_EQUAL} { return LESSER_EQUAL; }
  {GREATER_EQUAL} { return GREATER_EQUAL; }
  {NOT_EQUAL} { return NOT_EQUAL; }
  {EQUAL} { return EQUAL; }
  {UNARY_NOT} { return UNARY_NOT; }
  {PLUS} { return PLUS; }
  {MINUS} { return MINUS; }
  {LESSER} { return LESSER; }
  {GREATER} { return GREATER; }
  {AND} { return AND; }
  {OR} { return OR; }
  {TRUE} { return TRUE; }
  {FALSE} { return FALSE; }
  {STRING_LITERAL} { return STRING_LITERAL; }
  {INTEGRAL_LITERAL} { return INTEGRAL_LITERAL; }
  {IDENTIFIER} { return IDENTIFIER; }
  {COMMENT} { return COMMENT; }
[^] { return BAD_CHARACTER; }
}
