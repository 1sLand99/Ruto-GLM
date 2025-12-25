package com.rosan.installer.ext.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import com.rosan.installer.ext.process.IShizukuProcess
import com.rosan.installer.ext.process.ShizukuProcess
import com.rosan.installer.ext.util.coroutines.closeWith
import com.rosan.installer.ext.util.coroutines.closeWithException
import com.rosan.installer.ext.util.process.requireShizuku
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper

class ShizukuServiceManager : ServiceManager, KoinComponent {
    private val context by inject<Context>()

    private val args =
        Shizuku.UserServiceArgs(ComponentName(context, ShizukuProcess::class.java))
            .processNameSuffix(this.hashCode().toString(0xF))

    private val process by lazy {
        runBlocking { newProcess() }
    }

    private suspend fun newProcess() = requireShizuku {
        callbackFlow {
            val connection = object : ServiceConnection {
                override fun onServiceConnected(
                    name: ComponentName?,
                    service: IBinder?
                ) {
                    closeWith(IShizukuProcess.Stub.asInterface(service))
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    closeWithException(Exception(""))
                }
            }

            Shizuku.bindUserService(args, connection)
            awaitClose { }
        }.first()
    }

    override suspend fun ping(): Boolean =
// //        binderWrapper 不依赖 process，所以只检测权限是否可用
//        runCatching { process.isAlive }.getOrDefault(false)
        runCatching { requireShizuku {} }.isSuccess

    override suspend fun binderWrapper(binder: IBinder): IBinder =
        requireShizuku {
            ShizukuBinderWrapper(binder)
        }

    override suspend fun serviceBinder(className: String): IBinder =
        process.serviceBinder(className).binder

    override fun close() = try {
        process.destroy()
        Shizuku.unbindUserService(args, null, true)
    } catch (_: Throwable) {
    }
}