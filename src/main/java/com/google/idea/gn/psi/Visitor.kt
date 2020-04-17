//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.GnKeys
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class Visitor(scope: Scope, private val interceptor: VisitorDelegate = object : VisitorDelegate() {}) : GnVisitor() {

  private var stop = false

  enum class CallAction {
    SKIP,
    EXECUTE,
    VISIT_BLOCK
  }

  abstract class VisitorDelegate {
    open fun afterVisit(element: PsiElement, scope: Scope): Boolean = false
    open fun resolveCall(call: GnCall): CallAction = CallAction.EXECUTE
  }

  private fun interceptAfter(element: PsiElement) {
    stop = interceptor.afterVisit(element, scope)
  }

  override fun visitFile(file: PsiFile) {
    ProgressManager.checkCanceled()
    if (stop) {
      return
    }
    val gnFile = file as GnFile
    val statements = gnFile.findChildByClass(GnStatementList::class.java)
    statements?.let { visitStatementList(it) }
    interceptAfter(file)
  }

  override fun visitStatementList(statementList: GnStatementList) {
    if (stop) {
      return
    }
    ProgressManager.checkCanceled()
    for (statement in statementList.statementList) {
      visitStatement(statement)
      if (stop) {
        return
      }
    }
    interceptAfter(statementList)
  }

  override fun visitStatement(statement: GnStatement) {
    ProgressManager.checkCanceled()
    if (stop) {
      return
    }
    statement.call?.let { visitCall(it) } ?: statement.assignment?.let { visitAssignment(it) }
    ?: statement.condition?.let { visitCondition(it) } ?: return
    interceptAfter(statement)
  }

  override fun visitCall(call: GnCall) {
    ProgressManager.checkCanceled()
    if (stop) {
      return
    }
    val f = scope.getFunction(call.id.text)
    call.putUserData(GnKeys.CALL_RESOLVED_FUNCTION, f)
    when (interceptor.resolveCall(call)) {
      CallAction.SKIP -> Unit
      CallAction.EXECUTE -> f?.execute(call, scope)
      CallAction.VISIT_BLOCK -> call.block?.let { visitBlock(it, pushScope = true) }
    }
    interceptAfter(call)
  }

  fun visitBlock(block: GnBlock, pushScope: Boolean) {
    ProgressManager.checkCanceled()
    if (stop) {
      return
    }
    if (pushScope) {
      scope = BlockScope(scope)
    } else {
      assert(scope is BlockScope)
    }

    visitStatementList(block.statementList)
    interceptAfter(block)
    scope = scope.parent!!
  }

  override fun visitBlock(block: GnBlock) {
    visitBlock(block, pushScope = false)
  }

  override fun visitCondition(condition: GnCondition) {
    ProgressManager.checkCanceled()
    if (stop) {
      return
    }
    val result: GnValue? = GnPsiUtil.evaluate(condition.expr, scope)
    if (result?.bool == true) { // Visit statement list directly, there's no scope created by condition blocks.
      visitStatementList(condition.block.statementList)
      return
    }
    val elseCondition = condition.elseCondition ?: return
    val elseBlock = elseCondition.block
    if (elseBlock != null) {
      visitStatementList(elseBlock.statementList)
      return
    }
    val elseIfCondition = elseCondition.condition
    elseIfCondition?.let { visitCondition(it) }
  }

  override fun visitAssignment(assignment: GnAssignment) {
    ProgressManager.checkCanceled()
    if (stop) {
      return
    }
    val type = AssignType.fromNode(assignment.assignOp.firstChild.node.elementType) ?: return
    val target = createAssignmentTarget(assignment.lvalue, type) ?: return
    val result = GnPsiUtil.evaluate(assignment.expr, scope)

    target.variableValue = when (type) {
      AssignType.EQUAL -> result
      AssignType.PLUS_EQUAL -> result?.let { target.variableValue?.plusEqual(it) }
      AssignType.MINUS_EQUAL -> result?.let { target.variableValue?.minusEqual(it) }
    }
  }

  private interface AssignmentTarget {
    var variableValue: GnValue?
  }

  private class IdAssignmentTarget(val name: String, val scope: Scope, val loose: Boolean) : AssignmentTarget {
    override var variableValue: GnValue?
      get() = scope.getVariable(name)?.value
      set(value) {
        scope.getVariable(name)?.let { v ->
          v.value = value
          return
        }
        // Allow declaration if variable does not exist.
        if (loose) {
          scope.addVariable(Variable(name, value))
        }
      }
  }

  private class ScopeAccessAssignmentTarget(val variable: Variable,
                                            val fieldAccess: String,
                                            val loose: Boolean) : AssignmentTarget {
    override var variableValue: GnValue?
      get() = variable.value?.scope?.get(fieldAccess)
      set(value) {
        value?.let { v ->
          val scope = variable.value?.scope ?: return
          if (loose || scope.containsKey(fieldAccess)) {
            variable.value = GnValue(scope.plus(Pair(fieldAccess, v)))
          }
        }
      }
  }

  private class ArrayAssignmentTarget(val variable: Variable,
                                      val index: Int) : AssignmentTarget {
    override var variableValue: GnValue?
      get() = variable.value?.list?.get(index)
      set(value) {
        value?.let { replace ->
          variable.value?.list?.let { list ->
            variable.value = GnValue(list.mapIndexed { i, v -> if (i == index) replace else v })
          }
        }
      }
  }

  private fun createAssignmentTarget(lvalue: GnLvalue, type: AssignType): AssignmentTarget? {
    val loose = type == AssignType.EQUAL
    lvalue.id?.let { return IdAssignmentTarget(it.text, scope, loose) }
    lvalue.scopeAccess?.let { scopeAccess ->
      val first = scopeAccess.idList[0].text
      val second = scopeAccess.idList[1].text
      val variable = scope.getVariable(first) ?: return null
      variable.value?.scope ?: return null
      return ScopeAccessAssignmentTarget(variable, second, loose)
    }
    lvalue.arrayAccess?.let { arrayAccess ->
      val first = arrayAccess.id.text
      val variable = scope.getVariable(first) ?: return null
      val index = GnPsiUtil.evaluate(arrayAccess.expr, scope)?.int ?: return null
      if (index >= variable.value?.list?.size ?: 0) {
        return null
      }
      return ArrayAssignmentTarget(variable, index)
    }
    return null
  }


  private enum class AssignType {
    EQUAL, PLUS_EQUAL, MINUS_EQUAL;

    companion object {
      fun fromNode(type: IElementType): AssignType? {
        return when (type) {
          Types.EQUAL -> EQUAL
          Types.PLUS_EQUAL -> PLUS_EQUAL
          Types.MINUS_EQUAL -> MINUS_EQUAL
          else -> null
        }
      }
    }
  }

  var scope: Scope = scope
    private set
}