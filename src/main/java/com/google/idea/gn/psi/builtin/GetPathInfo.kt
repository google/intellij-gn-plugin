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
import com.intellij.util.PathUtil

class GetPathInfo : Function {
  override fun execute(call: GnCall, targetScope: Scope): GnValue? {
    val exprList = call.exprList.exprList
    if (exprList.size != 2) {
      return null
    }
    val path = GnPsiUtil.evaluate(exprList[0], targetScope)?.string
        ?: return null

    val what = GnPsiUtil.evaluate(exprList[1], targetScope)?.string ?: return null
    val str: String? = when (what) {
      "file" -> {
        //  The substring after the last slash in the path, including the name and
        //  extension. If the input ends in a slash, the empty string will be
        //  returned.
        //  "foo/bar.txt" => "bar.txt"
        //  "bar.txt" => "bar.txt"
        //  "foo/" => ""
        //  "" => ""
        PathUtil.getFileName(path)
      }
      "name" -> {
        // The substring of the file name not including the extension."foo/bar.txt" => "bar"
        // "foo/bar" => "bar"
        // "foo/" => ""

        // This is nasty. Do something better.
        val parts = PathUtil.getFileName(path).split(".")
        if (parts.size > 1) {
          parts.dropLast(1).joinToString(".")
        } else {
          parts.joinToString(".")
        }

      }
      "extension" -> {
        // The substring following the last period following the last slash, or the
        // empty string if not found . The period is not included .
        // "foo/bar.txt" => "txt"
        // "foo/bar" => ""
        PathUtil.getFileExtension(path)
      }
      "dir" -> {
        //  The directory portion of the name, not including the slash.
        //    "foo/bar.txt" => "foo"
        //    "//foo/bar" => "//foo"
        //    "foo" => "."
        //  The result will never end in a slash, so if the resulting is empty, the
        //  system ("/") or source ("//") roots, a "." will be appended such that it
        //  is always legal to append a slash and a filename and get a valid path.
        PathUtil.getParentPath(path)
      }
      "out_dir" -> {
        // The output file directory corresponding to the path of the given file,
        // not including a trailing slash.
        //   "//foo/bar/baz.txt" => "//out/Default/obj/foo/bar"
        null
      }
      "gen_dir" -> {
        // The generated file directory corresponding to the path of the given file,
        // not including a trailing slash.
        //   "//foo/bar/baz.txt" => "//out/Default/gen/foo/bar"
        null
      }
      "abspath" -> {
        // The full absolute path name to the file or directory. It will be resolved
        // relative to the current directory, and then the source- absolute version
        // will be returned. If the input is system- absolute, the same input will
        // be returned.
        //   "foo/bar.txt" => "//mydir/foo/bar.txt"
        //   "foo/" => "//mydir/foo/"
        //   "//foo/bar" => "//foo/bar"  (already absolute)
        //   "/usr/include" => "/usr/include"  (already absolute)
        // If you want to make the path relative to another directory, or to be
        // system-absolute, see rebase_path().
        null
      }
      else -> {
        null
      }
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
    const val NAME = "get_path_info"
  }
}
