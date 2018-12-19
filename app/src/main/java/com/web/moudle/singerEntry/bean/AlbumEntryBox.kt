package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class AlbumEntryBox(
        @JSONField(name = "albumlist")
        val albumList:ArrayList<AlbumEntryItem>,
        @JSONField(name = "albumnums")
        val num:String,
        @JSONField(name = "havemore")
        val haveMore:Int
)