package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

//**field list
data class HotSearchSug(
        @JSONField(name = "artist")
        val artistSugList: ArrayList<ArtistSug> = ArrayList(),
        @JSONField(name = "song")
        val songList: ArrayList<MusicSug> = ArrayList(),
        @JSONField(name = "playlist")
        val playList: ArrayList<SongSheetSug> = ArrayList()
)