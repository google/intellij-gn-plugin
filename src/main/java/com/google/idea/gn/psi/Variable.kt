//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

class Variable(val name: String) {
  constructor(name: String, value: GnValue?) : this(name) {
    this.value = value
  }

  var value: GnValue? = null

}