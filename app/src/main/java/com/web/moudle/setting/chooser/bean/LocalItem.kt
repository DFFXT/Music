package com.web.moudle.setting.chooser.bean

import androidx.annotation.IntDef

data class LocalItem(
        @LocalItemType val type: Int,
        val name:String,
        val abPath:String,
        val tagStart:String,
        val date:String
){

    companion object {
        const val TYPE_FILE=1
        const val TYPE_DIR=2
        const val TYPE_BACK=3
    }
    @IntDef(TYPE_BACK, TYPE_DIR, TYPE_FILE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class LocalItemType
}