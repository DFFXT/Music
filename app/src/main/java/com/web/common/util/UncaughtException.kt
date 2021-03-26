package com.web.common.util

import android.util.Log
import com.web.web.R

object UncaughtException:Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        e?.printStackTrace()
        Log.e("uncaughtException",ResUtil.getString(R.string.uncaughtException)+"-> Thread:${t?.name}")

        e?.printStackTrace(IOUtil.log())
    }
}