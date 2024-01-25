package com.web.app

import android.app.Application
import com.fxffxt.preferen.Config
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.skin.skincore.SkinManager
import com.skin.skincore.provider.DefaultProviderFactory
import com.skin.skincore.provider.ResourceProviderFactory
import com.tencent.bugly.crashreport.CrashReport
import com.web.common.constant.Constant
import com.web.moudle.net.proxy.InternetProxy
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication() {

    override fun onCreate() {
        super.onCreate()
        val configuration = resources.configuration
        configuration.fontScale = Constant.LocalConfig.fontScale
        context = this
        CrashReport.initCrashReport(this)
        Config.ctx = this


        //Thread.setDefaultUncaughtExceptionHandler(UncaughtException)

        Config.ctx = context

        InternetProxy.startProxy()
        Constant.LocalConfig.initPath()

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter(context)
        }

        SkinManager.init(this, 0, DefaultProviderFactory())
    }
    companion object {
        lateinit var context: Application
    }
}