package com.web.moudle.net.retrofit

import android.support.annotation.IntDef
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ConverterFactory(@TYPE private val convertType: Int) : Converter.Factory() {
    companion object {
        const val TYPE_JSON = 1
        const val TYPE_STRING = 2

        @IntDef(TYPE_JSON, TYPE_STRING)
        @Retention(AnnotationRetention.SOURCE)
        annotation class TYPE
    }

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        return when (convertType) {
            TYPE_JSON -> MConverter<Any>(type)
            TYPE_STRING -> StringConverter(type)
            else -> null
        }
    }
}