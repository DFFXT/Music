package com.web.common.base

import android.annotation.SuppressLint
import android.content.Context
import org.litepal.LitePalApplication

class MyApplication : LitePalApplication(){

    override fun onCreate() {
        super.onCreate()
        context= applicationContext
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context:Context
    }
}