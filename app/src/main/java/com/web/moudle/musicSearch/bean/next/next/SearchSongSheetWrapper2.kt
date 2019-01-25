package com.web.moudle.musicSearch.bean.next.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet

data class SearchSongSheetWrapper2 (
        @JSONField(name = "play_list")
        val songList:ArrayList<SimpleSongSheet>,
        @JSONField(name = "total")
        val total:Int
)