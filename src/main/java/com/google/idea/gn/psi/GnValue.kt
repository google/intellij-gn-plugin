//  Copyright (c) 2020 Google LLC All rights reserved.
//  Use of this source code is governed by a BSD-style
//  license that can be found in the LICENSE file.
package com.google.idea.gn.psi

class GnValue {
  val value: Any

  constructor(string: String) {
    value = string
  }

  constructor(bool: Boolean) {
    value = bool
  }

  constructor(int: Int) {
    value = int
  }

  constructor(scope: Map<String, GnValue>) {
    value = scope
  }

  constructor(list: List<GnValue>) {
    value = list
  }

  val string: String?
    get() = tryCast<String>()

  val int: Int?
    get() {
      tryCast<Int>()?.let { return it }
      return tryCast<Boolean>()?.let { if (it) 1 else 0 }
    }

  val bool: Boolean
    get() {
      tryCast<Boolean>()?.let { return it }
      return tryCast<Int>()?.let { it != 0 } ?: false
    }

  private inline fun <reified T> tryCast(): T? {
    if (value is T) {
      return value
    }
    return null
  }

  val scope: Map<String, GnValue>?
    get() = tryCast<Map<String, GnValue>>()

  val list: List<GnValue>?
    get() = tryCast<List<GnValue>>()


  fun and(other: GnValue): GnValue? = GnValue(bool && other.bool)
  fun or(other: GnValue): GnValue? = GnValue(bool || other.bool)
  fun greaterThan(other: GnValue): GnValue? = int?.let { l -> other.int?.let { r -> GnValue(l > r) } }
  fun greaterThanOrEqual(other: GnValue): GnValue? = int?.let { l -> other.int?.let { r -> GnValue(l >= r) } }
  fun lessThan(other: GnValue): GnValue? = int?.let { l -> other.int?.let { r -> GnValue(l < r) } }
  fun lessThanOrEqual(other: GnValue): GnValue? = int?.let { l -> other.int?.let { r -> GnValue(l <= r) } }

  fun plus(other: GnValue): GnValue? {
    string?.let { l -> return other.string?.let { r -> GnValue(l + r) } }
    int?.let { l -> return other.int?.let { r -> GnValue(l + r) } }
    list?.let { l -> return other.list?.let { r -> GnValue(l.plus(r)) } }
    return null
  }

  fun plusEqual(other: GnValue): GnValue {
    when (value) {
      is Int,
      is String -> return plus(other) ?: this
    }
    // List allows appending both a single element or an entire other list.
    list?.let { l -> return other.list?.let { r -> GnValue(l.plus(r)) } ?: GnValue(l.plus(other)) }
    return this
  }

  fun minusEqual(other: GnValue): GnValue {
    when (value) {
      is Int -> return minus(other) ?: this
    }
    // List allows removing both a single element or an entire other list.
    list?.let { l ->
      return other.list?.let { r -> GnValue(l.filter { !r.contains(it) }) }
          ?: GnValue(l.filter { it != other })
    }
    return this
  }

  fun minus(other: GnValue): GnValue? {
    int?.let { l -> return other.int?.let { r -> GnValue(l - r) } }
    list?.let { l -> return other.list?.let { r -> GnValue(l.filter { !r.contains(it) }) } }
    return null
  }

  override fun equals(other: Any?): Boolean {
    if (other !is GnValue) {
      return false
    }
    return value == other.value
  }

  override fun toString(): String = "GnValue($value)"
}