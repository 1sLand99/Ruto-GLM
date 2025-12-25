package com.rosan.ruto.device.repo

import com.rosan.ruto.service.IActivityManager
import com.rosan.ruto.service.IDisplayManager
import com.rosan.ruto.service.IImeManager
import com.rosan.ruto.service.IInputManager

interface DeviceRepo: AutoCloseable {
    val activityManager: IActivityManager

    val displayManager: IDisplayManager

    val inputManager: IInputManager

    val imeManager: IImeManager

    suspend fun waitMillis(millis: Long)
}