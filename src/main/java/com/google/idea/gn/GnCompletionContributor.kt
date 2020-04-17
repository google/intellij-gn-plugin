// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.completion.FileCompletionProvider
import com.google.idea.gn.completion.IdentifierCompletionProvider
import com.google.idea.gn.psi.Builtin
import com.google.idea.gn.psi.GnFile
import com.google.idea.gn.psi.Types
import com.google.idea.gn.psi.builtin.Import
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionInitializationContext
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.util.text.Matcher

class GnCompletionContributor : CompletionContributor() {

  companion object {
    val LOGGER = Logger.getInstance(GnCompletionContributor::class.toString())
    const val DUMMY_ID = "gn_dummy_completion_"
  }


  override fun beforeCompletion(context: CompletionInitializationContext) {
    context.dummyIdentifier = DUMMY_ID
    context.replacementOffset = 0
  }

  init {
    // Complete DEPS and PUBLIC_DEPS.
    extend(CompletionType.BASIC,
        psiElement(Types.STRING_LITERAL)
            .withAncestor(10,
                psiElement(Types.ASSIGNMENT).withFirstNonWhitespaceChild(
                    psiElement(Types.LVALUE)
                        .withText(
                            PlatformPatterns.string()
                                .oneOf(Builtin.DEPS.name, Builtin.PUBLIC_DEPS.name))
                )
            ), FileCompletionProvider(
        Matcher { name: String -> name == GnFile.BUILD_FILE },
        skipFileName = true, parseTargets = true)
    )

    // Complete imports.
    extend(CompletionType.BASIC, psiElement(Types.STRING_LITERAL)
        .withSuperParent(5, psiElement(Types.CALL)
            .withFirstNonWhitespaceChild(psiElement(Types.ID).withText(
                Import.NAME))),
        FileCompletionProvider(
            Matcher { name: String -> name.endsWith(GnFile.GNI) }))

    // Complete sources.
    extend(CompletionType.BASIC, psiElement(Types.STRING_LITERAL).withAncestor(10,
        psiElement(Types.ASSIGNMENT)
            .withFirstChild(
                psiElement(Types.LVALUE).withText(Builtin.SOURCES.name))),
        FileCompletionProvider(Matcher { true }))

    // Complete identifiers.
    extend(CompletionType.BASIC, psiElement(Types.IDENTIFIER), IdentifierCompletionProvider())
  }


}