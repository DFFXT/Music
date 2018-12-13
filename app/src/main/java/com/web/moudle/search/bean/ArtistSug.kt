package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

/**
 * 搜索歌手推荐
 */
data class ArtistSug(
        @JSONField(name = "artistname")
        val artistName: String,
        @JSONField(name = "artistId")
        val artistId: String,
        @JSONField(name = "artistpic")
        val artistPic: String,
        val weight: String
)