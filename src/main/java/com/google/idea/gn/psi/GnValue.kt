//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

class GnValue(val value: Any?) {

  val string: String?
    get() = if (value is String) {
      value
    } else null

  val bool: Boolean
    get() {
      if (value is Boolean) {
        return value
      }
      return if (value is Int) {
        value != 0
      } else false
    }
}