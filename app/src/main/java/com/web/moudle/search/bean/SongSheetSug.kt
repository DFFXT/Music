package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

data class SongSheetSug(
        @JSONField(name = "playlistid")
        val songSheetId:String,
        @JSONField(name = "playlistpic")
        val songSheetIcon:String,
        @JSONField(name = "playlistname")
        val songSheetName:String,
        @JSONField(name = "songids")
        val songIds:String
)