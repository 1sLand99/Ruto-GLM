package com.rosan.ruto.service

import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.IBinder
import android.os.ServiceManager
import android.os.SystemClock
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.Keep
import com.rosan.installer.ext.service.ShizukuServiceManager
import com.rosan.installer.ext.util.InputEventUtil
import kotlinx.coroutines.runBlocking
import kotlin.math.pow

class InputManagerService @Keep constructor(
    shizuku: ShizukuServiceManager,
    private val clickDuration: Long = 100,
    private val clickInterval: Long = 100,
    private val longClickDuration: Long = 1000,
    private val swipeDuration: Long = 1000
) : IInputManager.Stub() {
    companion object {
        const val INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH = 2
    }

    private val manager by lazy<android.hardware.input.IInputManager> {
        runBlocking {
            val binder = ServiceManager.getService(Context.INPUT_SERVICE) as IBinder
            val wrapper = shizuku.binderWrapper(binder)
            android.hardware.input.IInputManager.Stub.asInterface(wrapper)
        }
    }

    private fun injectEvent(
        ev: InputEvent, mode: Int = INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH, uid: Int = -1
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) manager.injectInputEventToTarget(
            ev,
            mode,
            uid
        )
        else manager.injectInputEvent(ev, mode)
    }

    private fun sendTouchEvent(
        x: Float, y: Float, action: Int, downTime: Long = SystemClock.uptimeMillis()
    ): Long {
        val eventTime = SystemClock.uptimeMillis()
        val event = MotionEvent.obtain(
            downTime, eventTime, action, x, y, 0
        )
        event.source = InputDevice.SOURCE_TOUCHSCREEN
        try {
            injectEvent(event)
            return downTime
        } finally {
            event.recycle()
        }
    }

    override fun click(p: PointF) {
        val downTime = sendTouchEvent(p.x, p.y, MotionEvent.ACTION_DOWN)
        Thread.sleep(clickDuration)
        sendTouchEvent(p.x, p.y, MotionEvent.ACTION_UP, downTime)
    }

    override fun doubleClick(p: PointF) {
        click(p)
        Thread.sleep(clickInterval)
        click(p)
    }

    override fun longClick(p: PointF) {
        val downTime = sendTouchEvent(p.x, p.y, MotionEvent.ACTION_DOWN)
        Thread.sleep(longClickDuration)
        sendTouchEvent(p.x, p.y, MotionEvent.ACTION_UP, downTime)
    }

    override fun swipe(
        start: PointF, end: PointF
    ) {
        // 按住
        val downTime = sendTouchEvent(start.x, start.y, MotionEvent.ACTION_DOWN)

        val t1 = swipeDuration / 11 * 9
        fun interpolator(x: Double, k: Double = 1.toDouble()): Double {
            return 3 * x.pow(2 * k) - 2 * x.pow(3 * k)
        }

        do {
            val x = (SystemClock.uptimeMillis() - downTime).toDouble() / t1
            if (x >= 1) {
                sendTouchEvent(end.x, end.y, MotionEvent.ACTION_MOVE, downTime)
                continue
            }
            val y = interpolator(x)
            val sx = start.x + (end.x - start.x) * y
            val sy = start.y + (end.y - start.y) * y
            sendTouchEvent(sx.toFloat(), sy.toFloat(), MotionEvent.ACTION_MOVE, downTime)
        } while (downTime + swipeDuration > SystemClock.uptimeMillis())

        // 抬起
        sendTouchEvent(end.x, end.y, MotionEvent.ACTION_UP, downTime)
    }

    private fun sendKeyEvent(
        keycode: Int, action: Int, downTime: Long = SystemClock.uptimeMillis()
    ): Long {
        val eventTime = SystemClock.uptimeMillis()
        val util = InputEventUtil(downTime, eventTime, action, keycode, 0)
        util.event.source = InputDevice.SOURCE_TOUCHSCREEN

        try {
            injectEvent(util.event)
            return downTime
        } finally {
            util.recycle()
        }
    }

    private fun clickKey(keycode: Int) {
        val downTime = sendKeyEvent(keycode, KeyEvent.ACTION_DOWN)
        Thread.sleep(clickDuration)
        sendKeyEvent(keycode, KeyEvent.ACTION_UP, downTime)
    }

    override fun clickBack() {
        clickKey(KeyEvent.KEYCODE_BACK)
    }

    override fun clickHome() {
        clickKey(KeyEvent.KEYCODE_HOME)
    }
}