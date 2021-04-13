// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.GnKeys
import com.google.idea.gn.GnLabel
import com.google.idea.gn.config.gnRoot
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.io.StringWriter

object GnPsiUtil {

  private val LOGGER = Logger.getInstance(GnPsiUtil.javaClass)

  fun evaluate(expr: GnExpr, scope: Scope, visitorDelegate: Visitor.VisitorDelegate? = null): GnValue? {
    LOGGER.debug("evaluating $expr [${expr.text}]")
    val result = when (expr) {
      is GnStringExpr -> evaluateStringExpr(expr, scope)
      is GnLiteralExpr -> evaluateLiteral(expr, scope)
      is GnPrimaryExpr -> evaluatePrimary(expr, scope, visitorDelegate)
      is GnUnaryExpr -> evaluateUnaryNot(expr, scope)
      is GnBinaryExpr -> expr.evaluate(scope)
      else -> null
    }

    LOGGER.debug("evaluating $expr = $result [${expr.text}]")
    return result
  }

  private fun evaluatePrimary(expr: GnPrimaryExpr,
                              scope: Scope,
                              visitorDelegate: Visitor.VisitorDelegate?): GnValue? {
    expr.id?.let { id ->
      val v = scope.getVariable(id.text) ?: return null
      return v.value
    }
    expr.scopeAccess?.let { scopeAccess ->
      return evaluateScopeAccess(scopeAccess, scope)
    }
    expr.arrayAccess?.let { arrayAccess ->
      return evaluateArrayAccess(arrayAccess, scope)
    }
    expr.collection?.let { collection ->
      return evaluateCollection(collection, scope)
    }
    expr.block?.let { block ->
      val blockScope = BlockScope(scope)
      block.accept(Visitor(blockScope, visitorDelegate ?: Visitor.VisitorDelegate()))
      return blockScope.intoValue()
    }
    expr.call?.let { call ->
      return scope.getFunction(call.id.text)?.let { func ->
        call.putUserData(GnKeys.CALL_RESOLVED_FUNCTION, func)
        func.execute(call, scope)
      }
    }
    return null
  }

  private fun evaluateCollection(collection: GnCollection, scope: Scope): GnValue? {
    return GnValue(collection.exprList.exprList.map { evaluate(it, scope) ?: return null })
  }

  private fun evaluateUnaryNot(expr: GnUnaryExpr, scope: Scope): GnValue? {
    return expr.expr?.let { inner -> evaluate(inner, scope)?.bool?.let { b -> GnValue(!b) } }
  }

  private fun evaluateArrayAccess(access: GnArrayAccess, scope: Scope): GnValue? {
    val id = access.id.text
    val index = evaluate(access.expr, scope)?.int ?: return null
    val list = scope.getVariable(id)?.value?.list ?: return null
    if (index < list.size) {
      return list[index]
    }
    return null
  }

  private fun evaluateScopeAccess(access: GnScopeAccess, scope: Scope): GnValue? {
    val left = access.idList[0]
    val right = access.idList[1]
    return scope.getVariable(left.text)?.value?.scope?.get(right.text)
  }

  private fun evaluateStringExpand(expand: GnStringExpand, scope: Scope): String? {
    expand.id?.let { ident ->
      return scope.getVariable(ident.text)?.value?.string
    }
    expand.scopeAccess?.let { scopeAccess ->
      return evaluateScopeAccess(scopeAccess, scope)?.string
    }
    return null
  }

  private fun evaluateStringInner(inner: GnStringInner, scope: Scope): String? {
    inner.stringLiteralExpr?.let { literal ->
      return literal.text
    }
    inner.stringIdent?.let { ident ->
      return scope.getVariable(ident.id.text)?.value?.string
    }
    inner.stringHex?.let { hex ->
      return hex.node.findChildByType(Types.HEX_BYTE)?.text?.substring(2)?.toInt(16)?.toChar()
          ?.toString()
    }
    inner.stringExpand?.let { expand ->
      return evaluateStringExpand(expand, scope)
    }
    return null
  }

  private fun evaluateStringExpr(expr: GnStringExpr, scope: Scope): GnValue? {
    val writer = StringWriter()
    for (inner in expr.stringInnerList) {
      val s = evaluateStringInner(inner, scope) ?: return null
      writer.append(s)
    }
    return GnValue(writer.toString())
  }

  private fun evaluateLiteral(literal: GnLiteralExpr, scope: Scope): GnValue? {
    val def = literal.firstChild
    if (def is GnStringExpr) {
      return evaluateStringExpr(def, scope)
    }
    return when (def.node.elementType) {
      Types.TRUE -> GnValue(true)
      Types.FALSE -> GnValue(false)
      Types.INTEGRAL_LITERAL -> try {
        GnValue(def.text.toInt())
      } catch (_e: NumberFormatException) {
        null
      }
      else -> null
    }
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
      labelLocation.project.gnRoot
    } else {
      labelLocation.originalFile.virtualFile.parent
    }?.let { VfsUtil.findRelativeFile(it, *label.parts) }
  }
}
