package com.rosan.installer.ext.process

import androidx.annotation.Keep
import com.rosan.app_process.NewProcess
import com.rosan.app_process.ParcelableBinder
import com.rosan.installer.ext.util.parcelable
import com.rosan.installer.ext.util.process.ProcessUtil
import kotlin.system.exitProcess

internal class ShizukuProcess @Keep constructor() :
    IShizukuProcess.Stub() {
    private val context = NewProcess.getUIDContext()

    init {
        ProcessUtil.koin(context)
    }

    override fun destroy() = exitProcess(0)

    override fun isAlive(): Boolean = true

    override fun serviceBinder(className: String): ParcelableBinder =
        ProcessUtil.binder(className, context).parcelable()
}