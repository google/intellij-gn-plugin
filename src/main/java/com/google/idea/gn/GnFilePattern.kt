// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn

class GnFilePattern(pattern: String) {
  val regex: Regex

  init {
    val replace = listOf("\\b", "*")
    val parts = mutableListOf<CharSequence>("^")
    var patt: CharSequence = pattern
    while (patt.isNotEmpty()) {
      val x = patt.findAnyOf(replace)

      val sub = x?.let {
        patt.subSequence(0, x.first)
      } ?: patt
      if (sub.isNotEmpty()) {
        parts.add(Regex.escape(sub.toString()))
      }

      patt = when (x?.second) {
        "\\b" -> {
          parts.add("(.*\\/)?")
          patt.subSequence(x.first + 2, patt.length)
        }
        "*" -> {
          parts.add(".*")
          patt.subSequence(x.first + 1, patt.length)
        }
        else -> {
          ""
        }
      }
    }
    parts.add("$")
    regex = Regex(parts.joinToString(""))
  }

  fun matches(chars: CharSequence): Boolean = regex.matches(chars)
}
