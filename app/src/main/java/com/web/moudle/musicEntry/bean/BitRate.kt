package com.web.moudle.musicEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class BitRate(
        @JSONField(name = "file_link")
        val songLink:String,
        @JSONField(name = "file_size")
        val fileSize:Long,
        @JSONField(name = "file_extension")
        val format:String,
        @JSONField(name = "file_duration")
        val duration:Int,
        @JSONField(name = "file_bitrate")
        val bitrate:Int
)