package com.web.moudle.search.bean

import androidx.annotation.IntDef
import org.litepal.crud.DataSupport

data class SearchResItem (
        val name:String,
        val itemId:String,
        val subName:String,
        @SearchItemType val type:Int
):DataSupport(){

    fun saveOrUpdateAsync() {
        saveOrUpdateAsync("itemId=? and name=?",itemId,name)
                .listen {  }
    }

    companion object {
        const val SearchItemType_Music=0
        const val SearchItemType_Artist=1
        const val SearchItemType_Album=2
        const val SearchItemType_Head=3
        const val SearchItemType_Sheet=4

        const val SearchItemType_Search=5

        @IntDef(SearchItemType_Album, SearchItemType_Artist, SearchItemType_Head, SearchItemType_Music)
        @Retention(AnnotationRetention.SOURCE)
        annotation class SearchItemType

    }
}