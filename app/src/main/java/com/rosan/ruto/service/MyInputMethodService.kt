package com.rosan.ruto.service

import android.inputmethodservice.InputMethodService
import android.os.IBinder
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection

class MyInputMethodService : InputMethodService(), IImeManager {
    companion object {
        var INSTANCE: MyInputMethodService? = null
            private set
    }

    var editorInfo: EditorInfo? = null
        private set

    var inputConnection: InputConnection? = null
        private set

    override fun onCreate() {
        INSTANCE = this
        super.onCreate()
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        this.editorInfo = editorInfo
        this.inputConnection = currentInputConnection
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        this.editorInfo = null
        this.inputConnection = null
    }

    private fun commitText(text: String, newCursorPosition: Int) =
        inputConnection?.commitText(text, newCursorPosition)

    private fun clearText() = inputConnection?.let {
        val beforeCursor = it.getTextBeforeCursor(Int.MAX_VALUE, 0)
        val before = beforeCursor?.length ?: 0
        val afterCursor = it.getTextAfterCursor(Int.MAX_VALUE, 0)
        val after = afterCursor?.length ?: 0

        if (before >= 0 || after > 0)
            it.deleteSurroundingText(before, after)
    }

    override fun readyInput() {
        TODO("Not yet implemented")
    }

    override fun finishInput() {
        TODO("Not yet implemented")
    }

    override fun text(text: String) {
        commitText(text, 1)
    }

    override fun print(code: String) {
        for (c in code) {
            commitText(c.toString(), 1)
            Thread.sleep(50)
        }
    }

    override fun clear() {
        clearText()
    }

    override fun asBinder(): IBinder {
        TODO("Not yet implemented")
    }
}