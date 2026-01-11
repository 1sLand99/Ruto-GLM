package com.rosan.ruto.device

import android.content.Context
import com.rosan.installer.ext.service.ServiceManager
import com.rosan.installer.ext.util.coroutines.SuspendLazy
import com.rosan.ruto.service.ActivityManagerService
import com.rosan.ruto.service.DisplayManagerService
import com.rosan.ruto.service.IActivityManager
import com.rosan.ruto.service.IDisplayManager
import com.rosan.ruto.service.IPackageManager
import com.rosan.ruto.service.ImeManagerService
import com.rosan.ruto.service.InputManagerService
import com.rosan.ruto.service.PackageManagerService

class DeviceManager(
    private val context: Context,
    val serviceManager: ServiceManager
) : AutoCloseable {
    private val packageManagerLoader = SuspendLazy {
        serviceManager.serviceBinder(PackageManagerService::class.java) {
            IPackageManager.Stub.asInterface(it)
        }
    }

    private val activityManagerLoader = SuspendLazy {
        serviceManager.serviceBinder(ActivityManagerService::class.java) {
            IActivityManager.Stub.asInterface(it)
        }
    }

    private val displayManagerLoader = SuspendLazy {
        serviceManager.serviceBinder(DisplayManagerService::class.java) {
            IDisplayManager.Stub.asInterface(it)
        }
    }

    private val inputManagerLoader = SuspendLazy {
        InputManagerService(serviceManager)
    }

    private val inputMethodManagerLoader = SuspendLazy {
        ImeManagerService(context, serviceManager)
    }

    suspend fun getPackageManager() = packageManagerLoader.get()

    suspend fun getActivityManager() = activityManagerLoader.get()

    suspend fun getDisplayManager() = displayManagerLoader.get()

    suspend fun getInputManager() = inputManagerLoader.get()

    suspend fun getInputMethodManager() = inputMethodManagerLoader.get()

    override fun close() {
        packageManagerLoader.clear()
        activityManagerLoader.clear()
        displayManagerLoader.clear()
        inputManagerLoader.clear()
        inputMethodManagerLoader.clear()
    }
}