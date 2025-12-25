package com.rosan.installer.ext.util

import android.view.KeyEvent

class InputEventUtil(
    val event: KeyEvent
) {

    constructor(downTime: Long, eventTime: Long, action: Int, code: Int, repeat: Int) :
            this(KeyEvent(downTime, eventTime, action, code, repeat))

    fun recycle() = try {
        val clazz = KeyEvent::class.java
        val method = clazz.getDeclaredMethod("recycle")
        method.isAccessible = true
        method.invoke(event)
    } catch (ignored: Throwable) {
    }
}