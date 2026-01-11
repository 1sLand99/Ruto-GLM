package com.rosan.installer.ext.util.coroutines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SuspendLazy<T>(private val initializer: suspend () -> T) {
    private var cached: T? = null
    private val mutex = Mutex()

    suspend fun get(): T {
        // 快速路径：已缓存直接返回
        val existing = cached
        if (existing != null) return existing

        // 慢速路径：加锁初始化
        return mutex.withLock {
            cached ?: initializer().also { cached = it }
        }
    }

    fun clear() {
        cached = null
    }
}