package com.web.common.constant

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.fxffxt.preferen.*
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.moudle.preference.SP
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity
import com.music.m.R
import com.web.moudle.setting.suffix.sp.ConfigImpl

/**
 * app 配置
 */
open class AppConfigCore(ctx: Context? = null): ConfigImpl {

    //private var ctx: Application? = null
    override val localFileName: String = Constant.spName
    private val sp by lazy {
        try {
            object :SharedPreferences by SP.getKV(localFileName){}
        } catch (_ : Throwable) {
            null
        }
    }
    var noNeedScan by noneNull(false)
    var lockScreenBgColor by noneNull(ResUtil.getColor(R.color.themeColor, ctx))
    var lockScreenBgImagePath:String? by nullable()
    var lockScreenBgMode by noneNull(LockScreenSettingActivity.BG_MODE_COLOR)
    var noLockScreen by noneNull(true)
    var currentSoundEffect by noneNull(0)
    //var currentVersion by int()

    var cacheEnable by noneNull(false)
    var customerCachePath by noneNull(Constant.LocalConfig.musicCachePath)
    var customerDownloadPath by noneNull(Constant.LocalConfig.musicDownloadPath)

    var lyricsColor by noneNull(ResUtil.getColor(R.color.themeColor, ctx))
    var lyricsFocusColor by noneNull(ResUtil.getColor(R.color.colorAccent, ctx))
    var lyricsSize by noneNull(ResUtil.getSize(R.dimen.textSize_normal, ctx))
    var lyricsOverlapOpen by noneNull(false)
    var isFloatWindowLocked by noneNull(false)
    var floatWindowX by noneNull(0)
    var floatWindowY by noneNull(ViewUtil.screenHeight(ctx)/2- ViewUtil.dpToPx(ctx,36f))

    var enableSystemMusic by noneNull(true)
    var lastMusic by nullable<String?>()

    override fun getSharedPreference(): SharedPreferences {
        return sp ?: super.getSharedPreference()
    }
}