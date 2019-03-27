package com.web.common.util

import android.util.Log
import com.web.common.base.MyApplication
import com.web.common.tool.MToast
import com.web.web.R

object UncaughtException:Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread?, e: Throwable?) {
        e?.printStackTrace()
        Log.e("uncaughtException",ResUtil.getString(R.string.uncaughtException)+"-> Thread:${t?.name}")
        IOUtil.log("${e?.message} \n ${e?.cause}")
    }
}