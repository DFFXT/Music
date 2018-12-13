package com.web.common.util

import android.graphics.Color
import android.view.View
import android.view.Window
import com.web.common.base.MyApplication

object WindowUtil {
    @JvmStatic
    fun setImmersedStatusBar(window:Window){
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * 获取状态栏高度
     */
    @JvmStatic
    fun getStatusHeight():Int{
        val resources = MyApplication.context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}