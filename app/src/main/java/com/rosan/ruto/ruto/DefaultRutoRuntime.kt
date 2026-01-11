package com.rosan.ruto.ruto

import android.graphics.PointF
import com.rosan.ruto.device.DeviceManager
import com.rosan.ruto.ruto.script.RutoRuntime

class DefaultRutoRuntime(
    private val deviceManager: DeviceManager,
    private val displayId: Int
) : RutoRuntime() {
    init {
        registerFunction("launch") {
            launch(arg(0))
        }
        registerFunction("click") {
            click(arg(0), arg(1))
        }
        registerFunction("double_click") {
            doubleClick(arg(0), arg(1))
        }
        registerFunction("long_click") {
            longClick(arg(0), arg(1))
        }
        registerFunction("swipe") {
            swipe(arg(0), arg(1), arg(2), arg(3))
        }
        registerFunction("back") {
            deviceManager.getInputManager().clickBack(displayId)
        }
        registerFunction("home") {
            deviceManager.getInputManager().clickHome(displayId)
        }

        registerFunction("text") {
            deviceManager.getInputMethodManager().text(args.joinToString(""))
        }
        registerFunction("type") {
            deviceManager.getInputMethodManager().print(args.joinToString(""))
        }
        registerFunction("clear") {
            deviceManager.getInputMethodManager().clear()
        }
        registerFunction("wait") {
            Thread.sleep(arg<Long>(0) * 1000)
        }
    }

    private suspend fun launch(name: String) {
        deviceManager.getActivityManager().startLabel(name, displayId)
    }

    private suspend fun click(xt: Float, yt: Float) {
        val displayInfo = deviceManager.getDisplayManager().getDisplayInfo(displayId)
        val x = xt * displayInfo.logicalWidth / 1000
        val y = yt * displayInfo.logicalHeight / 1000
        deviceManager.getInputManager().click(PointF(x, y), displayId)
    }

    private suspend fun doubleClick(xt: Float, yt: Float) {
        val displayInfo = deviceManager.getDisplayManager().getDisplayInfo(displayId)
        val x = xt * displayInfo.logicalWidth / 1000
        val y = yt * displayInfo.logicalHeight / 1000
        deviceManager.getInputManager().doubleClick(PointF(x, y), displayId)
    }

    private suspend fun longClick(xt: Float, yt: Float) {
        val displayInfo = deviceManager.getDisplayManager().getDisplayInfo(displayId)
        val x = xt * displayInfo.logicalWidth / 1000
        val y = yt * displayInfo.logicalHeight / 1000
        deviceManager.getInputManager().longClick(PointF(x, y), displayId)
    }

    private suspend fun swipe(x1t: Float, y1t: Float, x2t: Float, y2t: Float) {
        val displayInfo = deviceManager.getDisplayManager().getDisplayInfo(displayId)
        val x1 = x1t * displayInfo.logicalWidth / 1000
        val y1 = y1t * displayInfo.logicalHeight / 1000
        val x2 = x2t * displayInfo.logicalWidth / 1000
        val y2 = y2t * displayInfo.logicalHeight / 1000
        deviceManager.getInputManager().swipe(PointF(x1, y1), PointF(x2, y2), displayId)
    }
}