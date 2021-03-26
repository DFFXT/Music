package com.web.app

import android.annotation.SuppressLint
import android.content.Context
import com.fxffxt.preferen.Config
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.tencent.bugly.crashreport.CrashReport
import com.web.common.constant.Constant
import com.web.moudle.net.proxy.InternetProxy
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication(){

    override fun onCreate() {
        super.onCreate()
        val configuration = resources.configuration
        configuration.fontScale = Constant.LocalConfig.fontScale
        context = applicationContext.createConfigurationContext(configuration)

        CrashReport.initCrashReport(this)


        //Thread.setDefaultUncaughtExceptionHandler(UncaughtException)

        Config.ctx = context

        InternetProxy.startProxy()
        Constant.LocalConfig.initPath()

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }


    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }
}