package com.web.moudle.net.retrofit

import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
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
        val response=chain.proceed(request.build())
        return@addInterceptor response.newBuilder().body(object :ResponseBody(){
            val body=response.body()
            override fun contentLength(): Long { return body?.contentLength()?:0}
            override fun contentType(): MediaType? { return body?.contentType() }
            override fun source(): BufferedSource {
                return Okio.buffer(object :ForwardingSource(body!!.source()){
                    override fun read(sink: Buffer, byteCount: Long): Long {
                        val len=super.read(sink, byteCount)
                        return len
                    }
                })
            }
        }).build()
    }.build()
    private var retrofit:Retrofit = Retrofit.Builder()
            .baseUrl("http://tingapi.ting.baidu.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ConverterFactory(ConverterFactory.TYPE_JSON))
            .client(client)
            .build()

    private var retrofitNoConverter=Retrofit.Builder()
            .baseUrl("http://tingapi.ting.baidu.com/")
            .addConverterFactory(ConverterFactory(ConverterFactory.TYPE_STRING))
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
    fun <T:Any> obtainClassNoConverter(t:Class<T>):T{
        return retrofitNoConverter.create(t)
    }

    private var redirectClient:OkHttpClient?=null
    private var noRedirectRetrofit:Retrofit?=null
    //**禁止重定向
    fun <T:Any> obtainClassNoConverterNoRedirect(t:Class<T>):T{
        if(redirectClient==null){
            redirectClient=OkHttpClient.Builder()
                    .followRedirects(false)
                    .addInterceptor { chain ->
                val request=chain.request()
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent","xxx")
                return@addInterceptor chain.proceed(request.build())
            }.build()
            noRedirectRetrofit=Retrofit.Builder()
                    .baseUrl("http://tingapi.ting.baidu.com/")
                    .addConverterFactory(ConverterFactory(ConverterFactory.TYPE_STRING))
                    .client(redirectClient!!)
                    .build()
        }
        return noRedirectRetrofit!!.create(t)
    }
}