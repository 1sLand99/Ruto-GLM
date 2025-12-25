package com.rosan.installer.ext.service

import android.content.ComponentName
import android.content.Context
import android.os.IBinder
import com.rosan.app_process.AppProcess
import com.rosan.installer.ext.exception.RootNotWorkException
import com.rosan.installer.ext.exception.TerminalNotWorkException
import com.rosan.installer.ext.util.closeQuietly
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.StringTokenizer

class TerminalServiceManager(private val shell: String) : ServiceManager, KoinComponent {
    private val context by inject<Context>()

    private val command = mutableListOf<String>().also {
        val st = StringTokenizer(shell)
        while (st.hasMoreTokens()) it.add(st.nextToken())
    }

    private val terminal by lazy {
        object : AppProcess.Terminal() {
            override fun newTerminal(): List<String> = command
        }.also {
            if (it.init(context)) return@also
            if (command.firstOrNull() == "su") throw RootNotWorkException()
            throw TerminalNotWorkException("Terminal start failed. Check permissions or commands: $shell")
        }
    }

    override suspend fun ping(): Boolean = runCatching { terminal.init() }.isSuccess

    override suspend fun binderWrapper(binder: IBinder): IBinder = terminal.binderWrapper(binder)

    override suspend fun serviceBinder(className: String): IBinder =
        terminal.serviceBinder(ComponentName(context, className))

    override fun close() {
        terminal.closeQuietly()
    }
}