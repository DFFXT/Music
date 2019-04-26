package com.web.moudle.net.retrofit

import com.alibaba.fastjson.JSON
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

class MConverter<T>(val type: Type) : Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T {
        val res = value.string()
        if (res.startsWith("(")) {
            return JSON.parseObject(res.substring(1, res.length - 1), type)
        }
        return JSON.parseObject(res, type)
    }
}

class StringConverter(val type: Type) : Converter<ResponseBody, String> {
    override fun convert(value: ResponseBody): String {
        return value.string()
    }
}