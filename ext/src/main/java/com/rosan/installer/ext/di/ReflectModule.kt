package com.rosan.installer.ext.di

import android.os.Build
import org.koin.dsl.module
import org.lsposed.hiddenapibypass.HiddenApiBypass

val reflectModule = module {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        HiddenApiBypass.addHiddenApiExemptions("")
}