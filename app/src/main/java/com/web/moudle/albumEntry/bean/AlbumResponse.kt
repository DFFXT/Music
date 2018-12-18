package com.web.moudle.albumEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class AlbumResponse(
        @JSONField(name = "albumInfo")
        val albumInfo: AlbumInfo,
        @JSONField(name = "is_collect")
        val collect:Int,
        @JSONField(name = "share_pic")
        val sharePic:String,
        @JSONField(name = "share_title")
        val shareTitle:String,
        @JSONField(name = "songlist")
        val otherSong: ArrayList<OtherSong>
)