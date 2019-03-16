package com.web.common.base

import android.annotation.SuppressLint
import android.content.Context
import com.web.common.constant.Apk
import com.web.common.constant.Constant
import com.web.common.util.UncaughtException
import com.web.moudle.net.proxy.InternetProxy
import io.flutter.view.FlutterMain
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication(){

    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException)
        context= applicationContext

        InternetProxy.startProxy()
        FlutterMain.startInitialization(this)
        //proxy= HttpProxyCacheServer(this)
        Constant.LocalConfig.initPath()
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
        //@JvmStatic
        //var proxy:HttpProxyCacheServer?=null
    }
}