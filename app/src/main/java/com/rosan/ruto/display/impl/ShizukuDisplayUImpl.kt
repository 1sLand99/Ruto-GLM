package com.rosan.ruto.display.impl

import android.hardware.display.DisplayManager_Hidden
import android.hardware.display.VirtualDisplay
import android.os.Build
import android.view.Surface
import androidx.annotation.RequiresApi
import com.rosan.ruto.display.repo.DisplayManagerRepo
import java.util.concurrent.ConcurrentHashMap

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
class DisplayUManagerRepo : DisplayManagerRepo() {
    private val displayMap = ConcurrentHashMap<Int, VirtualDisplay>()

    override fun monitor(displayId: Int, surface: Surface): Int {
        val displayInfo = getDisplayInfo(displayId)
        val name = "ruto-mirror:${surface.hashCode()}"
        val width = displayInfo.logicalWidth
        val height = displayInfo.logicalHeight
        val virtualDisplay = DisplayManager_Hidden.createVirtualDisplay(
            name,
            width,
            height,
            displayId,
            surface
        )
        val displayId = virtualDisplay!!.display.displayId
        displayMap[displayId] = virtualDisplay
        return displayId
    }

    override fun release(monitorId: Int) {
        displayMap.computeIfPresent(monitorId) { _, display ->
            display.release()
            return@computeIfPresent null
        }
    }

    override fun releaseAll() {
        displayMap.keys.forEach { release(it) }
    }
}