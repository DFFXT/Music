package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

data class MusicSug(
        var songName: String,
        var songId: String,
        var artistName: String,
        @JSONField(name = "encrypted_songid")
        var encodeSongId: String
) {
}