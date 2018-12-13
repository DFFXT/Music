package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

/**
 * 搜索专辑推荐
 */
data class AlbumSug(
        @JSONField(name = "albumname")
        var albumName: String,
        @JSONField(name = "artistname")
        var artistName: String,
        @JSONField(name = "artistpic")
        var artistPic: String,
        @JSONField(name = "albumid")
        var albumId: String,
        @JSONField(name = "weight")
        var weight: String
)