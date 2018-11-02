package com.web.common.util

import android.util.Log

object UncaughtException:Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        Log.e("uncaughtException","未捕获的异常")
        e?.printStackTrace()
    }
}