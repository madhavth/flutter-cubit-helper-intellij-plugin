package com.madhavth.flutter_cubit_plugin.action

import com.intellij.openapi.ui.DialogWrapper
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.BadLocationException

class GenerateBlocDialog(private val listener: Listener, private val modelNames: List<String>) :
    DialogWrapper(null) {
    private var blocNameTextField: JTextField? = null
    private var uiCodeGenerateCheckBox: JCheckBox? = null
    private var spinnerModelNames: JSpinner? = null
    private var checkBoxGenerateBlocProvider: JCheckBox? = null
    private var contentPanel: JPanel? = null
    private var textFieldApiUrl: JTextField? = null
    private var labelApiUrl: JLabel? = null
    private var labelModelName: JLabel? = null

    private var currentModel: String? = null

    override fun createCenterPanel(): JComponent? {
        return contentPanel
    }

    override fun doOKAction() {
        super.doOKAction()
        listener.onGenerateBlocClicked(
            blocNameTextField!!.text, true, currentModel,
            uiCodeGenerateCheckBox?.isSelected ?: false,
            addBlocProvider = checkBoxGenerateBlocProvider?.isSelected ?: true,
            textFieldApiUrl?.text ?: ""
        )
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return blocNameTextField
    }

    interface Listener {
        fun onGenerateBlocClicked(
            blocName: String?, shouldUseEquatable: Boolean, modelName: String?,
            generateUiCode: Boolean = false,
            addBlocProvider: Boolean = true,
            apiUrl: String = ""
        )
    }

    fun showHideApiFields(show: Boolean) {
        labelApiUrl?.isVisible = show
        textFieldApiUrl?.isVisible = show
        labelModelName?.isVisible = show

        if (show) {
            if (modelNames.contains(currentModel)) {
                labelModelName?.isVisible = false
            } else {
                labelModelName?.text = "$currentModel not found"
            }
        }
    }


    init {
        init()
        showHideApiFields(false)
        spinnerModelNames?.model = SpinnerListModel(modelNames)

        val jtf: JTextField = (spinnerModelNames?.editor as JSpinner.DefaultEditor).textField
//        jtf.isEditable = false

        jtf.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(p0: DocumentEvent?) {
                showChangedValue(p0)
            }

            override fun removeUpdate(p0: DocumentEvent?) {
                showChangedValue(p0)
            }

            override fun changedUpdate(p0: DocumentEvent?) {
                showChangedValue(p0)
            }

            private fun showChangedValue(e: DocumentEvent?) {
                if (e == null) return
                try {

                    val text = e.document.getText(0, e.document.length)
                    if (text == null || text.isEmpty()) {
                        if (currentModel.isNullOrBlank()) {
                            showHideApiFields(false)
                        }
                        return
                    }
                    currentModel = text

                    showHideApiFields(true)

                } catch (e1: BadLocationException) {
                    //handle if you want
                } catch (e1: NumberFormatException) {
                }
            }

        })

        checkBoxGenerateBlocProvider?.isSelected = true
    }
}