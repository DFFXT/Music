package com.web.common.util

import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

object KeyboardManager {
    @JvmStatic
    fun requestKeyboard(v:View){
        v.requestFocus()
        val manager=v.context.getSystemService(InputMethodManager::class.java)
        manager.showSoftInput(v,InputMethodManager.SHOW_FORCED)
    }
    @JvmStatic
    fun hideKeyboard(ctx:Context,windowToken:IBinder){
        val manager=ctx.getSystemService(InputMethodManager::class.java)
        manager.hideSoftInputFromWindow(windowToken,0)
    }
}