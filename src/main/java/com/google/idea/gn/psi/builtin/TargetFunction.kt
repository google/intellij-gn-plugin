//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.psi.*
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.Target
import com.google.idea.gn.psi.scope.Scope

abstract class TargetFunction : Function() {
  // TODO look at SmartPsiElementPointer
  override fun execute(call: GnCall, targetScope: Scope) {
    val targetName = GnPsiUtil.evaluateFirstToString(call.exprList, targetScope) ?: return
    var callSite = targetScope.callSite
    if (callSite == null) {
      callSite = call
    }
    targetScope.addTarget(Target(targetName, callSite))
  }

  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.TARGET_FUNCTION

  override val isBuiltin: Boolean
    get() = true
}