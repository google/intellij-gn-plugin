// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.psi

enum class GnCompositeType(val primitive: GnValue.Type, val inner: GnValue.Type? = null) {
  BOOL(GnValue.Type.BOOL),
  STRING(GnValue.Type.STRING),
  INT(GnValue.Type.INT),
  SCOPE(GnValue.Type.SCOPE),
  LIST(GnValue.Type.LIST),
  LIST_OF_STRING(GnValue.Type.LIST, GnValue.Type.STRING);

  override fun toString(): String = if (inner != null) {
    "$primitive of $inner"
  } else {
    primitive.toString()
  }


}
