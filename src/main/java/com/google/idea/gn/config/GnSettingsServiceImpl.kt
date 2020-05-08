// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.config

import com.intellij.configurationStore.serializeObjectInto
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializer
import org.jdom.Element


private const val serviceName: String = "GnSettings"

@com.intellij.openapi.components.State(name = serviceName, storages = [
  Storage(StoragePathMacros.WORKSPACE_FILE),
  Storage("misc.xml", deprecated = true)
])
class GnSettingsServiceImpl(
    private val project: Project
) : PersistentStateComponent<Element>, GnSettingsService {
  @Volatile
  private var state: GnSettingsService.State = GnSettingsService.State()

  override fun getState(): Element {
    val element = Element(serviceName)
    serializeObjectInto(state, element)
    return element
  }

  override fun loadState(element: Element) {
    val rawState = element.clone()
    XmlSerializer.deserializeInto(state, rawState)
  }

  override val projectRoot: String?
    get() = state.projectRoot

  override fun modify(action: (GnSettingsService.State) -> Unit) {
    val newState = state.copy().also(action)
    if (state != newState) {
      state = newState.copy()
    }
  }

}
