package com.rosan.installer.ext.util

import android.os.IBinder

class IInputMethodManagerUtil(
    private val binder: IBinder
) {
    fun setImeEnabled(id: String, enabled: Boolean) {
        val enabled = if (enabled) "enable" else "disable"
        binder.shellCommand("ime", enabled, id)
    }

    fun switchToTargetIme(id: String) {
        binder.shellCommand("ime", "set", id)
    }
}