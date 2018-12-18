package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

/**
 * 搜索专辑推荐
 */
data class AlbumSug(
        @JSONField(name = "albumname")
        val albumName: String,
        @JSONField(name = "artistname")
        val artistName: String,
        @JSONField(name = "artistpic")
        val artistPic: String,
        @JSONField(name = "albumid")
        val albumId: Long,
        @JSONField(name = "weight")
        val weight: String
)