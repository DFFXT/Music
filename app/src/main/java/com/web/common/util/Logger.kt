package com.web.common.util

import android.util.Log

object Logger {
    fun d(tag: String, msg: String?) {
        Log.i(tag, msg ?: "null")
    }
}