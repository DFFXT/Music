package com.web.app

import android.app.Application
import com.fxffxt.preferen.Config
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.tencent.bugly.crashreport.CrashReport
import com.web.common.constant.Constant
import com.web.moudle.net.proxy.InternetProxy
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication() {

    override fun onCreate() {
        super.onCreate()
        val configuration = resources.configuration
        configuration.fontScale = Constant.LocalConfig.fontScale
        app = this
        CrashReport.initCrashReport(this)


        //Thread.setDefaultUncaughtExceptionHandler(UncaughtException)


        InternetProxy.startProxy()
        Constant.LocalConfig.initPath()

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }

        SkinManager.init(this, 0, DefaultProviderFactory())
    }
    companion object {
        private var app: Application? = null
        val context: Application
            get() {
                if (app == null){
                    app = Application()
                }
                return app!!
            }
    }
}