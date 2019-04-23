package com.web.common.base

import android.annotation.SuppressLint
import android.content.Context
import com.web.common.constant.Apk
import com.web.common.constant.Constant
import com.web.common.util.UncaughtException
import com.web.moudle.net.proxy.InternetProxy
import com.web.moudle.songSheetEntry.adapter.JSEngine
import io.flutter.view.FlutterMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication(){

    override fun onCreate() {
        super.onCreate()
        val configuration = resources.configuration
        configuration.fontScale = Constant.LocalConfig.fontScale
        context=applicationContext.createConfigurationContext(configuration)

        Thread.setDefaultUncaughtExceptionHandler(UncaughtException)


        InternetProxy.startProxy()
        FlutterMain.startInitialization(this)
        Constant.LocalConfig.initPath()

        GlobalScope.launch(Dispatchers.IO) {
            JSEngine().s()
        }


    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }
}