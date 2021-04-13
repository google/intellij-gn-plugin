// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.psi.scope.Scope

class SplitList : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    if (exprList.size < 2) {
      return null
    }
    val list = GnPsiUtil.evaluate(exprList[0], targetScope)?.list ?: return null
    val n = GnPsiUtil.evaluate(exprList[1], targetScope)?.int ?: return null
    if (n <= 0) {
      return null
    }
    var per = list.size / n
    var mod = list.size - (per * n)
    if (mod != 0) {
      per++
    }
    var take = 0
    val ret = mutableListOf<GnValue>()
    while (take + per <= list.size && per != 0) {
      ret.add(GnValue(list.subList(take, take + per)))
      take += per
      if (mod > 0) {
        mod--
        if (mod == 0) {
          per--
        }
      }
    }
    if (take < list.size) {
      ret.add(GnValue(list.subList(take, list.size)))
    }
    while (ret.size < n) {
      ret.add(GnValue(emptyList()))
    }
    return GnValue(ret)
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "split_list"
  }
}
