package com.web.common.util

import android.support.annotation.StringRes
import org.litepal.LitePalApplication
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object StrUtil {
    @JvmStatic
    fun getString(@StringRes id: Int): String {
        return LitePalApplication.getContext().resources.getString(id)
    }
    @JvmStatic
    fun timeFormat(pattern: String, time: Long): String {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        return format.format(Date(time))
    }
    @JvmStatic
    fun getFileSize(size:Int):String{
        val format= DecimalFormat("0.00")
        return when(size){
            in 0..1024*1024-> format.format(size/1024F)+"KB"
            else->format.format(size/1024F/1024)+"MB"
        }
    }
}