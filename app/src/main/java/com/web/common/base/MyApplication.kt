package com.web.common.base

import android.annotation.SuppressLint
import android.content.Context
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.tencent.bugly.crashreport.CrashReport
import com.web.common.constant.Constant
import com.web.common.util.UncaughtException
import com.web.moudle.net.proxy.InternetProxy
import io.flutter.view.FlutterMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication(){

    override fun onCreate() {
        super.onCreate()
        val configuration = resources.configuration
        configuration.fontScale = Constant.LocalConfig.fontScale
        context=applicationContext.createConfigurationContext(configuration)

        CrashReport.initCrashReport(this)

        //Thread.setDefaultUncaughtExceptionHandler(UncaughtException)


        InternetProxy.startProxy()
        FlutterMain.startInitialization(context)
        FlutterMain.ensureInitializationComplete(context,null)
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