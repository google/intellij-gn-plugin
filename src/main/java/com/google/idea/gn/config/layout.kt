// Copyright (c) 2020 Google LLC All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.
package com.google.idea.gn.config

import com.intellij.openapi.Disposable
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.openapi.ui.TextComponentAccessor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.IdeBorderFactory
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent

const val HGAP = 30
const val VERTICAL_OFFSET = 2
const val HORIZONTAL_OFFSET = 5

fun layout(block: LayoutBuilder.() -> Unit): JPanel {
  val panel = JPanel(BorderLayout())
  val innerPanel = JPanel().apply {
    layout = BoxLayout(this, BoxLayout.Y_AXIS)
  }
  panel.add(innerPanel, BorderLayout.NORTH)
  val builder = LayoutBuilderImpl(innerPanel).apply(block)
  UIUtil.mergeComponentsWithAnchor(builder.labeledComponents)
  return panel
}

interface LayoutBuilder {
  fun row(text: String = "", component: JComponent, toolTip: String = "")
  fun block(text: String, block: LayoutBuilder.() -> Unit)
}

private class LayoutBuilderImpl(
    val panel: JPanel,
    val labeledComponents: MutableList<LabeledComponent<*>> = mutableListOf()
) : LayoutBuilder {
  override fun block(text: String, block: LayoutBuilder.() -> Unit) {
    val blockPanel = JPanel().apply {
      layout = BoxLayout(this, BoxLayout.Y_AXIS)
      border = IdeBorderFactory.createTitledBorder(text, false)
    }
    LayoutBuilderImpl(blockPanel, labeledComponents).apply(block)
    panel.add(blockPanel)
  }

  override fun row(text: String, component: JComponent, toolTip: String) {
    val labeledComponent = LabeledComponent.create(component, text, BorderLayout.WEST).apply {
      (layout as? BorderLayout)?.hgap = HGAP
      border = JBUI.Borders.empty(VERTICAL_OFFSET, HORIZONTAL_OFFSET)
    }
    labeledComponent.toolTipText = toolTip.trimIndent()
    labeledComponents += labeledComponent
    panel.add(labeledComponent)
  }
}

fun pathToDirectoryTextField(
    disposable: Disposable,
    title: String,
    onTextChanged: () -> Unit = {}
): TextFieldWithBrowseButton {

  val component = TextFieldWithBrowseButton(null, disposable)
  component.addBrowseFolderListener(title, null, null,
      FileChooserDescriptorFactory.createSingleFolderDescriptor(),
      TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
  )
  component.childComponent.document.addDocumentListener(object : DocumentAdapter() {
    override fun textChanged(e: DocumentEvent) {
      onTextChanged()
    }
  })

  return component
}
