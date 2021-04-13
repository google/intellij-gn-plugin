// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

import com.google.idea.gn.util.getPathLabel
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiFile
import java.io.StringWriter
import java.util.regex.Pattern

class GnLabel private constructor() {
  private var targetField: String? = null
  val target: String?
    get() = if (targetField == null && parts.isNotEmpty()) {
      parts[parts.size - 1]
    } else targetField

  private fun writeDir(writer: StringWriter) {
    if (isAbsolute) {
      writer.write("//")
    }
    writer.write(java.lang.String.join("/", *parts))
  }

  fun toFullyQualified(): GnLabel {
    val ret = GnLabel()
    ret.isAbsolute = isAbsolute
    ret.parts = parts
    ret.targetField = target
    ret.toolchain = toolchain
    return ret
  }

  fun dropToolChain(): GnLabel {
    val ret = GnLabel()
    ret.isAbsolute = isAbsolute
    ret.parts = parts
    ret.targetField = targetField
    ret.toolchain = null
    return ret
  }

  override fun toString(): String {
    val writer = StringWriter()
    writeDir(writer)
    targetField?.let {
      if (it.isNotEmpty()) {
        writer.write(":")
        writer.write(it)
      }
    }
    toolchain?.let {
      if (it.isNotEmpty()) {
        writer.write("(")
        writer.write(it)
        writer.write(")")
      }
    }
    return writer.toString()
  }

  var isAbsolute = false
    private set
  var parts: Array<String> = emptyArray()
    private set

  var toolchain: String? = null
    private set

  val dirString: String
    get() {
      val writer = StringWriter()
      writeDir(writer)
      return writer.toString()
    }

  fun toAbsolute(file: PsiFile): GnLabel? {
    if (isAbsolute) {
      return this
    }
    val ret = parse(getPathLabel(file)) ?: return null
    ret.isAbsolute = true
    ret.toolchain = toolchain
    ret.targetField = target
    ret.parts = ret.parts.plus(parts)
    return ret
  }

  companion object {
    private val PATH_PATTERN = Pattern.compile(
        """(//)?([a-zA-Z0-9$ \-_./]*)(:[a-zA-Z0-9_\-$.]*)?(\([a-zA-Z0-9_\-$./:]*\))?""")

    @kotlin.jvm.JvmStatic
    fun parse(path: String?): GnLabel? {
      if (path.isNullOrEmpty()) {
        return null
      }
      val out = GnLabel()
      val m = PATH_PATTERN.matcher(path)
      if (!m.matches()) {
        return null
      }
      if (m.group(1) != null) {
        out.isAbsolute = true
      }
      out.parts = StringUtil.split(m.group(2), "/", true, true).toTypedArray()
      out.targetField = m.group(3)?.substring(1)

      if (out.parts.isEmpty() && out.targetField == null) {
        return null
      }
      out.toolchain = m.group(4)
      if (out.toolchain != null) {
        out.toolchain = out.toolchain!!.substring(1, out.toolchain!!.length - 1)
      }
      return out
    }
  }
}
