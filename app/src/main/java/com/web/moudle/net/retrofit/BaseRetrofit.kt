package com.web.moudle.net.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.ConcurrentHashMap


open class BaseRetrofit {
    private  val map=ConcurrentHashMap<String,Any>(5)
    private var retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("http://mobilecdn.kugou.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ConverterFactory())
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