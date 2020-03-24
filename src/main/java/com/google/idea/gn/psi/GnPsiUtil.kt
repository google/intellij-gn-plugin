//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.GnLabel
import com.google.idea.gn.psi.scope.Scope
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

object GnPsiUtil {
  fun evaluate(expr: GnExpr, scope: Scope): GnValue? =
      when (expr) {
        is GnLiteralExpr -> evaluateLiteral(expr)
        is GnPrimaryExpr -> evaluatePrimary(expr, scope)
        else -> null
      }


  private fun evaluatePrimary(expr: GnPrimaryExpr, scope: Scope): GnValue? {
    val id = expr.id
    if (id != null) {
      val v = scope.getVariable(id.text) ?: return null
      return v.value
    }
    return null
  }

  private fun evaluateLiteral(literal: GnLiteralExpr): GnValue? {
    val def = literal.firstChild
    if (def != null && Types.STRING_LITERAL == def.node.elementType) {
      val text = getUnquotedText(def)
      return GnValue(text)
    }
    return null
  }

  fun evaluateFirst(exprList: GnExprList, scope: Scope): GnValue? {
    val exprs = exprList.exprList
    return if (exprs.isEmpty()) {
      null
    } else evaluate(exprs[0], scope)
  }

  fun evaluateFirstToString(exprList: GnExprList, scope: Scope): String? {
    return resolveTo(evaluateFirst(exprList, scope), String::class.java)
  }

  fun <T> resolveTo(eval: GnValue?, tClass: Class<T>): T? {
    val o = eval?.value
    return if (!tClass.isInstance(eval?.value)) {
      null
    } else tClass.cast(o)
  }

  fun findPsiFile(labelLocation: PsiFile, label: GnLabel): PsiFile? {
    var virtualFile: VirtualFile = findVirtualFile(labelLocation, label) ?: return null
    if (virtualFile.isDirectory) {
      virtualFile = virtualFile.findChild(GnFile.BUILD_FILE) ?: return null
    }
    return PsiManager.getInstance(labelLocation.project).findFile(virtualFile)
  }

  fun findVirtualFile(labelLocation: PsiFile,
                      label: GnLabel): VirtualFile? {
    return if (label.isAbsolute) {
      labelLocation.project.guessProjectDir()
    } else {
      labelLocation.originalFile.virtualFile.parent
    }?.let { VfsUtil.findRelativeFile(it, *label.parts) }
  }

  fun getUnquotedText(element: PsiElement): String {
    var text = element.text
    if (text.startsWith("\"")) {
      text = text.substring(1)
    }
    if (text.endsWith("\"")) {
      text = text.substring(0, text.length - 1)
    }
    return text
  }
}