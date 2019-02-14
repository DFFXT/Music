package com.web.common.base

import android.annotation.SuppressLint
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer
import com.web.common.util.UncaughtException
import com.web.moudle.net.proxy.InternetProxy
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication(){

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException)
        context= applicationContext
        InternetProxy.startProxy()
        //proxy= HttpProxyCacheServer(this)
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
        //@JvmStatic
        //var proxy:HttpProxyCacheServer?=null
    }
}