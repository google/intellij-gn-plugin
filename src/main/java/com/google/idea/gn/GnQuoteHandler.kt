// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler

class GnQuoteHandler : SimpleTokenSetQuoteHandler(GnParserDefinition.STRING_EXPR)
