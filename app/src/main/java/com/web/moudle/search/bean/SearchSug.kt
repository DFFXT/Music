package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

data class SearchSug(
        @JSONField(name = "song")
        val musicSugList: ArrayList<MusicSug>,
        @JSONField(name = "album")
        val albumList: ArrayList<AlbumSug>,
        @JSONField(name = "artist")
        val artistList: ArrayList<ArtistSug>
)
