//package com.rosan.ruto.ruto
//
//import android.graphics.PointF
//import android.view.DisplayInfo
//import com.rosan.ruto.autoglm.script.AutoGLMRuntime
//import com.rosan.ruto.ruto.script.RutoRuntimeException
//import com.rosan.ruto.device.impl.ShizukuDeviceImpl
//
//class ShizukuGLMRuntime(
//    private val device: ShizukuDeviceImpl, private val displayId: Int
//) : AutoGLMRuntime() {
//    init {
//        defFunction(name = "do") {
//            if (!it.containsKey("action")) throw RutoRuntimeException("require action in do function")
//            return@defFunction doAction(it["action"], it)
//        }
//
//        defFunction(name = "finish") {
//            throw RutoFinishException(it.getOrDefault("message", "").toString())
//        }
//    }
//
//    private fun doAction(name: Any?, it: Map<String, Any>) {
//        when (name) {
//            "Launch" -> doActionLaunch(it["app"] as String)
//            "Tap" -> doActionTap(it["element"] as List<Int>)
//            "Double Tap" -> doActionDoubleTap(it["element"] as List<Int>)
//            "Long Press" -> doActionLongPress(it["element"] as List<Int>)
//            "Swipe" -> doActionSwipe(it["start"] as List<Int>, it["end"] as List<Int>)
//
//            "Back" -> doActionBack()
//            "Home" -> doActionHome()
//            "Wait" -> doActionWait()
//
//            "Type" -> doActionType(it["text"] as String)
//            "Type_Name" -> doActionTypeName(it["text"] as String)
////            else -> throw AutoGLMRuntimeException("action $name not support now.")
//        }
//    }
//
//    private fun getDisplayInfo(): DisplayInfo {
//        return device.displayManager.getDisplayInfo(displayId)
//    }
//
//    private fun doActionLaunch(app: String) {
//        device.activityManager.startLabel(app)
//    }
//
//    private fun doActionTap(list: List<Int>) {
//        val displayInfo = getDisplayInfo()
//        val point = PointF(
//            displayInfo.logicalWidth.toFloat() * list.first() / 1000,
//            displayInfo.logicalHeight.toFloat() * list.last() / 1000
//        )
//        device.inputManager.click(point)
//    }
//
//    private fun doActionDoubleTap(list: List<Int>) {
//        val displayInfo = getDisplayInfo()
//        val point = PointF(
//            displayInfo.logicalWidth.toFloat() * list.first() / 1000,
//            displayInfo.logicalHeight.toFloat() * list.last() / 1000
//        )
//        device.inputManager.doubleClick(point)
//    }
//
//    private fun doActionLongPress(list: List<Int>) {
//        val displayInfo = getDisplayInfo()
//        val point = PointF(
//            displayInfo.logicalWidth.toFloat() * list.first() / 1000,
//            displayInfo.logicalHeight.toFloat() * list.last() / 1000
//        )
//        device.inputManager.longClick(point)
//    }
//
//    private fun doActionSwipe(list1: List<Int>, list2: List<Int>) {
//        val displayInfo = getDisplayInfo()
//        val point1 = PointF(
//            displayInfo.logicalWidth.toFloat() * list1.first() / 1000,
//            displayInfo.logicalHeight.toFloat() * list1.last() / 1000
//        )
//        val point2 = PointF(
//            displayInfo.logicalWidth.toFloat() * list2.first() / 1000,
//            displayInfo.logicalHeight.toFloat() * list2.last() / 1000
//        )
//        device.inputManager.swipe(point1, point2)
//    }
//
//    private fun doActionBack() {
//        device.inputManager.clickBack()
//    }
//
//    private fun doActionHome() {
//        device.inputManager.clickHome()
//    }
//
//    private fun doActionWait() {
//        Thread.sleep(2000)
//    }
//
//    private fun doActionType(text: String) {
//        device.imeManager.readyInput()
//        Thread.sleep(200)
//        device.imeManager.text(text)
//        device.imeManager.finishInput()
//        Thread.sleep(200)
//    }
//
//    private fun doActionTypeName(text: String) {
//        device.imeManager.readyInput()
//        Thread.sleep(200)
//        device.imeManager.print(text)
//        device.imeManager.finishInput()
//        Thread.sleep(200)
//    }
//}
//
