package com.rosan.ruto.service

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.rosan.installer.ext.util.pendingActivity

class ActivityManagerService @Keep constructor(private val context: Context) :
    IActivityManager.Stub() {
    // package manager func
    private val packageManager by lazy {
        context.packageManager
    }

    override fun startLabel(label: String) {
        val apps = packageManager.getInstalledApplications(0)
        val app = apps.find {
            it.loadLabel(packageManager).contains(label, ignoreCase = true)
        } ?: return
        startApp(app.packageName)
    }

    override fun startApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        startActivity(intent)
    }

    override fun startActivity(intent: Intent) {
        intent.pendingActivity(context, intent.hashCode()).send()
    }
}