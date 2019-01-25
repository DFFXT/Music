package com.web.moudle.musicSearch.bean.next.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo

data class SearchArtistWrapper2 (
        @JSONField(name = "artist_list")
        val artistList:ArrayList<SimpleArtistInfo>,
        @JSONField(name = "total")
        val total:Int
)