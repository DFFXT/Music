package com.web.misc

import android.view.MotionEvent
import com.web.common.util.ViewUtil
import kotlin.math.abs

class MotionEventControl {
    var originRowX = 0f
    var originRowY = 0f
    var originX = 0f
    var originY = 0f
    private var preX = 0f
    private var preY = 0f
    var clickListener: (() -> Unit)? = null
    var moveListener: ((dx: Float, dy: Float) -> Unit)? = null
    val moveLimiteDistance = ViewUtil.dpToPx(10f)
    private var moved = false


    fun onTouchEvent(e: MotionEvent) {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                originRowX = e.rawX
                originRowY = e.rawY
                originX=e.x
                originY=e.y
                preX = e.rawX
                preY = e.rawY
                moved = false
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(e.rawX - originRowX) + abs(e.rawY - originRowY) > moveLimiteDistance) {
                    moved = true
                }
                if (moved) {
                    moveListener?.invoke(e.rawX - preX, e.rawY - preY)
                }
                preX = e.rawX
                preY = e.rawY
            }
            MotionEvent.ACTION_UP -> {
                if (!moved) {
                    clickListener?.invoke()
                }
            }
        }
    }
}