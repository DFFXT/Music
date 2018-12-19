package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

//**field list
data class HotSearchSug(
        @JSONField(name = "artist")
        val artistSugList: ArrayList<ArtistSug>,
        @JSONField(name = "song")
        val songList:ArrayList<MusicSug>
        //**支持playlist
        /*@JSONField(name = "playlist")
        val playList:ArrayList<Any>*/
)