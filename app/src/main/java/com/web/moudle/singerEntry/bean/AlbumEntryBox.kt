package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo

data class AlbumEntryBox(
        @JSONField(name = "albumlist")
        val albumList:ArrayList<SimpleAlbumInfo>?,
        @JSONField(name = "albumnums")
        val num:String?,
        @JSONField(name = "havemore")
        val haveMore:Int
)