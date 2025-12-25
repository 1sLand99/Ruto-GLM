package com.rosan.ruto.service

import android.os.Build
import androidx.annotation.Keep
import com.rosan.ruto.display.impl.DisplayLowerUManagerRepo
import com.rosan.ruto.display.impl.DisplayUManagerRepo
import com.rosan.ruto.display.repo.DisplayManagerRepo

class DisplayManagerService @Keep constructor(
    private val proxy: DisplayManagerRepo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) DisplayUManagerRepo()
        else DisplayLowerUManagerRepo()
) : IDisplayManager by proxy