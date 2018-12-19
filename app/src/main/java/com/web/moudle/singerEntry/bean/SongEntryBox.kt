package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class SongEntryBox(
        @JSONField(name = "songlist")
        val songList:ArrayList<SongEntryItem>,
        @JSONField(name = "songnums")
        val num:String,
        @JSONField(name = "havemore")
        val haveMore:Int
)