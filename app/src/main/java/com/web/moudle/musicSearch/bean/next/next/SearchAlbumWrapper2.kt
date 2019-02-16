package com.web.moudle.musicSearch.bean.next.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo

data class SearchAlbumWrapper2 (
        @JSONField(name = "album_list")
        val albumList:ArrayList<SimpleAlbumInfo>?= ArrayList(),
        @JSONField(name = "total")
        val total:Int
)