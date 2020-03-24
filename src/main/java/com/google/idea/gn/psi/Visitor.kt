//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

import com.google.idea.gn.psi.Visitor.AssignType
import com.google.idea.gn.psi.scope.BlockScope
import com.google.idea.gn.psi.scope.Scope
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType

class Visitor(scope: Scope) : GnVisitor() {

  override fun visitFile(file: PsiFile) {
    val gnFile = file as GnFile
    val statements = gnFile.findChildByClass(GnStatementList::class.java)
    statements?.let { visitStatementList(it) }
  }

  override fun visitStatementList(statementList: GnStatementList) {
    for (statement in statementList.statementList) {
      visitStatement(statement)
    }
  }

  override fun visitStatement(statement: GnStatement) {
    val call = statement.call
    if (call != null) {
      visitCall(call)
      return
    }
    val assignment = statement.assignment
    if (assignment != null) {
      visitAssignment(assignment)
      return
    }
    val condition = statement.condition
    condition?.let { visitCondition(it) }
  }

  override fun visitCall(call: GnCall) {
    val f = scope.getFunction(call.id.text)
    f?.execute(call, scope)
  }

  override fun visitBlock(block: GnBlock) {
    scope = BlockScope(scope)
    visitStatementList(block.statementList)
    scope = scope.parent!!
  }

  override fun visitCondition(condition: GnCondition) {
    val result: GnValue? = GnPsiUtil.evaluate(condition.expr, scope)
    if (result != null && result.bool) { // Visit statement list directly, there's no scope created by condition blocks.
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
    val lvalue = assignment.lvalue
    val type = AssignType.fromNode(assignment.assignOp.firstChild.node.elementType)
    if (lvalue.id != null) { // Id assignment
      val varName = lvalue.id!!.text
      var v = scope.getVariable(varName)
      if (v == null) {
        if (type != AssignType.EQUAL) { // We don't know this variable.
          return
        }
        v = Variable(varName)
        scope.addVariable(v)
      }
      if (type == AssignType.EQUAL) {
        v.value = GnPsiUtil.evaluate(assignment.expr, scope)
      }
    }
  }

  private enum class AssignType {
    EQUAL, PLUS_EQUAL, MINUS_EQUAL;

    companion object {
      fun fromNode(type: IElementType): AssignType? {
        if (Types.EQUAL == type) {
          return EQUAL
        }
        if (Types.PLUS_EQUAL == type) {
          return PLUS_EQUAL
        }
        return if (Types.MINUS_EQUAL == type) {
          MINUS_EQUAL
        } else null
      }
    }
  }

  var scope: Scope = scope
    private set
}