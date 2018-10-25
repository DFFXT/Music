package com.web.moudle.net.retrofit

import android.util.Log
import com.alibaba.fastjson.JSON
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

class MConVerter<T>(val type: Type):Converter<ResponseBody,T> {
    override fun convert(value: ResponseBody): T {
        val s=value.string()
        Log.i("log",s)
        return JSON.parseObject(s,type)
    }

}