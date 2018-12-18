package com.web.moudle.musicEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class MusicDetailInfo (
        @JSONField(name = "songinfo")
        val songInfo:SongInfo,
        @JSONField(name = "bitrate")
        val bitRate: BitRate
)