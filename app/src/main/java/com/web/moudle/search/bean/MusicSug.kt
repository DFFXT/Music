package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

data class MusicSug(
        @JSONField(name = "songname")
        var songName: String,
        @JSONField(name = "songid")
        var songId: String,
        @JSONField(name = "artistname")
        var artistName: String,
        @JSONField(name = "encrypted_songid")
        var encodeSongId: String?=null
)