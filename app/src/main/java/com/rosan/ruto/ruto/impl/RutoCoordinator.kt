package com.rosan.ruto.ruto.impl

import android.content.Context
import com.rosan.ruto.data.AppDatabase
import com.rosan.ruto.device.DeviceManager
import com.rosan.ruto.ruto.observer.RutoAiTasker
import com.rosan.ruto.ruto.observer.RutoResponder
import com.rosan.ruto.ruto.repo.RutoObserver
import kotlinx.coroutines.CoroutineScope

class RutoCoordinator(
    context: Context,
    database: AppDatabase,
    deviceManager: DeviceManager
) : RutoObserver {
    private val tasks = listOf(
        RutoResponder(database),
        RutoAiTasker(context, database, deviceManager)
    )

    override fun onInitialize(scope: CoroutineScope) {
        for (observer in tasks) {
            observer.onInitialize(scope)
        }
    }

    override fun onDestroy() {
        for (observer in tasks) {
            observer.onDestroy()
        }
    }
}