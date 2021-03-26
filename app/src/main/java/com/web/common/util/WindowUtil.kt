package com.web.common.util

import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.web.app.MyApplication


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

    /**
     * 设置是否全屏
     */
    @JvmStatic
    fun setFullScreen(window: Window, full: Boolean){
        val lp = window.attributes
        if (!full) {//设置为非全屏
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            window.attributes = lp
            //window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {//设置为全屏
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
            window.attributes = lp
            //window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    @JvmStatic
    fun setStatusBarTextDark(window: Window, dark: Boolean) {
        val decor = window.decorView
        if (dark) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

}