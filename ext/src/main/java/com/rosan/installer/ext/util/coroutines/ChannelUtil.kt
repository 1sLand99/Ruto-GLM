package com.rosan.installer.ext.util.coroutines

import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.channels.SendChannel

// 关闭关发送
fun <E> SendChannel<E>.closeWith(element: E): ChannelResult<Unit> {
    val result = trySend(element)
    close()
    return result
}

// 关闭并抛出
fun <E> SendChannel<E>.closeWithException(exception: Throwable): Boolean {
    return close(exception)
}