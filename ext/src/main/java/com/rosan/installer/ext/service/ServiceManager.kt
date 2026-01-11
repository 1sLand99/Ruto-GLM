package com.rosan.installer.ext.service

import android.os.IBinder
import java.io.Closeable

interface ServiceManager : Closeable {
    suspend fun isAlive(): Boolean = try {
        ensureConnected()
        true
    } catch (e: Exception) {
        false
    }

    suspend fun ensureConnected()

    suspend fun binderWrapper(binder: IBinder): IBinder

    suspend fun serviceBinder(className: String): IBinder

    suspend fun serviceBinder(clazz: Class<*>): IBinder =
        serviceBinder(clazz.name)

    suspend fun <T> serviceBinder(className: String, action: (IBinder) -> T): T =
        action.invoke(serviceBinder(className))

    suspend fun <T> serviceBinder(clazz: Class<*>, action: (IBinder) -> T): T =
        action.invoke(serviceBinder(clazz))
}