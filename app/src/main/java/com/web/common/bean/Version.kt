package com.web.common.bean

import com.web.common.constant.Constant
import com.web.moudle.music.player.bean.DiskObject
import com.web.moudle.preference.SP

data class Version (
        val versionCode:Long,
        val versionName:String,
        val publishTime:String,
        val apkLink:String,
        val desc:String
):DiskObject{
    override var path: String=""

    fun saveAsCurrent(){
        SP.putValue(Constant.spName,Constant.SpKey.currentVersion,this)
    }
    fun saveAsLast(){
        SP.putValue(Constant.spName,Constant.SpKey.latestVersion,this)
    }

    companion object {
        @JvmStatic
        fun readCurrentVersion():Version?{
            return SP.getDiskObject(Constant.spName,Constant.SpKey.currentVersion)
        }
        @JvmStatic
        fun readLatestVersion():Version?{
            return SP.getDiskObject(Constant.spName,Constant.SpKey.latestVersion)
        }
    }

}