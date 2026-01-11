package com.rosan.installer.ext.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.rosan.installer.ext.process.IShizukuProcess
import com.rosan.installer.ext.process.ShizukuProcess
import com.rosan.installer.ext.util.coroutines.SuspendLazy
import com.rosan.installer.ext.util.coroutines.closeWith
import com.rosan.installer.ext.util.coroutines.closeWithException
import com.rosan.installer.ext.util.process.requireShizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper

class ShizukuServiceManager(context: Context) : ServiceManager, KoinComponent {
    private var args = Shizuku.UserServiceArgs(ComponentName(context, ShizukuProcess::class.java))
        .processNameSuffix(this.hashCode().toString(0xF)).daemon(false)

    private val processLoader = SuspendLazy {
        withContext(Dispatchers.IO) {
            callbackFlow {
                val connection = object : ServiceConnection {
                    override fun onServiceConnected(
                        name: ComponentName?, service: IBinder?
                    ) {
                        closeWith(IShizukuProcess.Stub.asInterface(service))
                    }

                    override fun onServiceDisconnected(name: ComponentName?) {}
                }
                runCatching {
                    requireShizuku {
                        Shizuku.bindUserService(args, connection)
                    }
                }.getOrElse { closeWithException(it) }
                awaitClose()
            }.first()
        }
    }

    override suspend fun ensureConnected() {
        requireShizuku { }
    }

    override suspend fun binderWrapper(binder: IBinder): IBinder = requireShizuku {
        ShizukuBinderWrapper(binder)
    }

    override suspend fun serviceBinder(className: String): IBinder =
        processLoader.get().serviceBinder(className).binder

    override fun close() {
        runCatching {
            processLoader.clear()
            args?.let { Shizuku.unbindUserService(it, null, true) }
        }
    }
}