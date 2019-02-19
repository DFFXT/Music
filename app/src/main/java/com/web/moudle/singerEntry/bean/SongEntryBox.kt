package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import retrofit2.http.GET

data class SongEntryBox(
        @JSONField(name = "songlist")
        val songList: ArrayList<SimpleMusicInfo>?= arrayListOf(),
        @JSONField(name = "songnums")
        val total: Int,
        @JSONField(name = "havemore")
        val haveMore: Int
)