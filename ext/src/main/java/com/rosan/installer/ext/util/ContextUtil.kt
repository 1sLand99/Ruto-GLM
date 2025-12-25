package com.rosan.installer.ext.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

fun runOnUiThread(action: () -> Unit) {
    Handler(Looper.getMainLooper()).post {
        action.invoke()
    }
}

fun Context.openActivity(intent: Intent) {
    runOnUiThread {
        val activity = findActivity()
        if (activity == null) startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        else activity.startActivity(intent)
    }
}

fun Context.findActivity(): Activity? {
    if (this is Activity) return this
    if (this is ContextWrapper) return baseContext.findActivity()
    return null
}

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        Toast.makeText(this, text, duration).show()
    }
}

fun Context.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    runOnUiThread {
        toast(getString(resId), duration)
    }
}