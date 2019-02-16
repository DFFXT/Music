package com.web.moudle.musicSearch.bean.next.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo

data class SearchSongWrapper2 (
        @JSONField(name = "song_list")
        val songList:ArrayList<SimpleMusicInfo>?= arrayListOf(),
        @JSONField(name = "total")
        val total:Int
)