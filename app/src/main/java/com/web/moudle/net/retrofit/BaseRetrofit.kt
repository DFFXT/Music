package com.web.moudle.net.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.ConcurrentHashMap


open class BaseRetrofit {
    private val map=ConcurrentHashMap<String,Any>(5)
    private val client=OkHttpClient.Builder().addInterceptor { chain ->
        val request=chain.request()
                .newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent","xxx")
        return@addInterceptor chain.proceed(request.build())
    }.build()
    private var retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("http://tingapi.ting.baidu.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ConverterFactory())
            .client(client)
            .build()


    @Suppress("UNCHECKED_CAST")
    fun <T:Any> obtainClass(t:Class<T>):T{
        var cl:T?
        cl= map[t.name] as T?
        if(cl==null){
            cl=retrofit.create(t)
            map[t.name]=cl
        }
        return cl!!
    }
}