package com.web.common.constant

import android.content.SharedPreferences
import com.fxffxt.preferen.*
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.moudle.preference.SP
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity
import com.music.m.R

/**
 * app 配置
 */
object AppConfig:Config {
    override val localFileName: String = Constant.spName
    private val sp = object :SharedPreferences by SP.getKV(localFileName){}
    var noNeedScan by noneNull(false)
    var lockScreenBgColor by noneNull(ResUtil.getColor(R.color.themeColor))
    var lockScreenBgImagePath:String? by nullable()
    var lockScreenBgMode by noneNull(LockScreenSettingActivity.BG_MODE_COLOR)
    var noLockScreen by noneNull(true)
    var currentSoundEffect by noneNull(0)
    //var currentVersion by int()

    var cacheEnable by noneNull(false)
    var customerCachePath by noneNull(Constant.LocalConfig.musicCachePath)
    var customerDownloadPath by noneNull(Constant.LocalConfig.musicDownloadPath)

    var lyricsColor by noneNull(ResUtil.getColor(R.color.themeColor))
    var lyricsFocusColor by noneNull(ResUtil.getColor(R.color.colorAccent))
    var lyricsSize by noneNull(ResUtil.getSize(R.dimen.textSize_normal))
    var lyricsOverlapOpen by noneNull(false)
    var isFloatWindowLocked by noneNull(false)
    var floatWindowX by noneNull(0)
    var floatWindowY by noneNull(ViewUtil.screenHeight()/2- ViewUtil.dpToPx(36f))

    var enableSystemMusic by noneNull(true)
    var lastMusic by nullable<String?>()

    override fun getSharedPreference(): SharedPreferences {
        return sp
    }
}