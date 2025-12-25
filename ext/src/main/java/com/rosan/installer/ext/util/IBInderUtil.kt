package com.rosan.installer.ext.util

import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Parcel
import android.os.ResultReceiver
import android.util.Log
import java.io.File

fun IBinder.shellCommand(vararg args: String) {
    val file = File("/dev/null")
    file.createNewFile()
    val input = file.inputStream()
    val output = file.outputStream()

    val data = Parcel.obtain()
    val reply = Parcel.obtain()
    data.writeFileDescriptor(input.fd)
    data.writeFileDescriptor(output.fd)
    data.writeFileDescriptor(output.fd)
    data.writeStringArray(args)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        data.writeParcelable(null, 0)
    ResultReceiver(Handler(Looper.getMainLooper())).writeToParcel(data, 0)
    try {
        Log.e("r0s", "shell command ${args.joinToString(",")}")
        this.transact(1598246212, data, reply, 0)
    } finally {
        input.close()
        output.close()
        data.recycle()
        reply.recycle()
    }
}