package com.web.common.bean

data class Version (
        val versionCode:Long,
        val versionName:String,
        val publishTime:String,
        val apkLink:String,
        val desc:String
)