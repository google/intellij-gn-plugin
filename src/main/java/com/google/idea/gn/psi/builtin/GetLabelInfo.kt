// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi.builtin

import com.google.idea.gn.GnLabel
import com.google.idea.gn.completion.CompletionIdentifier
import com.google.idea.gn.psi.Function
import com.google.idea.gn.psi.GnCall
import com.google.idea.gn.psi.GnPsiUtil
import com.google.idea.gn.psi.GnValue
import com.google.idea.gn.psi.scope.Scope

class GetLabelInfo : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    if (exprList.size != 2) {
      return null
    }
    val label = GnPsiUtil.evaluate(exprList[0], targetScope)?.string?.let {
      GnLabel.parse(it)?.toAbsolute(call.containingFile)
    }
        ?: return null

    val what = GnPsiUtil.evaluate(exprList[1], targetScope)?.string ?: return null
    val str = when (what) {
      "name" -> {
        // The short name of the target. This will match the value of the
        // "target_name" variable inside that target's declaration. For the label
        // "//foo/bar:baz" this will return "baz".
        label.target
      }
      "dir" -> {
        // The directory containing the target's definition, with no slash at the
        // end. For the label "//foo/bar:baz" this will return "//foo/bar".
        label.dirString
      }
      "target_gen_dir" -> {
        // The generated file directory for the target. This will match the value of
        // the "target_gen_dir" variable when inside that target's declaration.
        null
      }
      "root_gen_dir" -> {
        // The root of the generated file tree for the target. This will match the
        // value of the "root_gen_dir" variable when inside that target's
        // declaration.
        null
      }
      "target_out_dir" -> {
        // The output directory for the target. This will match the value of the
        // "target_out_dir" variable when inside that target's declaration.
        null
      }
      "root_out_dir" -> {
        // The root of the output file tree for the target. This will match the
        // value of the "root_out_dir" variable when inside that target's
        // declaration.
        null
      }
      "label_no_toolchain" -> {
        // The fully qualified version of this label, not including the toolchain.
        // For the input ":bar" it might return "//foo:bar".
        label.toFullyQualified().dropToolChain().toString()
      }
      "label_with_toolchain" -> {
        // The fully qualified version of this label, including the toolchain. For
        // the input ":bar" it might return "//foo:bar(//toolchain:x64)".
        label.toFullyQualified().toString()
      }
      "toolchain" -> {
        // The label of the toolchain. This will match the value of the
        //  "current_toolchain" variable when inside that target's declaration.
        // TODO this should fallback to the default_toolchain
        label.toolchain
      }
      else -> null
    }

    return str?.let { GnValue(it) }
  }

  override val isBuiltin: Boolean
    get() = true
  override val identifierName: String
    get() = NAME
  override val identifierType: CompletionIdentifier.IdentifierType
    get() = CompletionIdentifier.IdentifierType.FUNCTION

  companion object {
    const val NAME = "get_label_info"
  }
}
