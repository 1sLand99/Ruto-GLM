package com.rosan.installer.ext.util

import android.os.IBinder
import com.rosan.app_process.ParcelableBinder

fun IBinder.parcelable() = ParcelableBinder(this)