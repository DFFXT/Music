package com.web.common.base

import android.view.MotionEvent
import com.web.common.tool.TouchHelper

open class BaseDragHelper : TouchHelper() {
    override fun touchDirection(distance: Float) {

    }

    override fun longOnClick(x: Float, y: Float): Boolean = false

    override fun longClickMove(dx: Float, dy: Float) {
    }

    override fun longClickUpNoMove(x: Float, y: Float) {
    }

    override fun longClickUpMoved(x: Float, y: Float) {
    }

    override fun down(event: MotionEvent?) {
    }
}