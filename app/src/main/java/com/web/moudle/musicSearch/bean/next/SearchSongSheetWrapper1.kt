package com.web.moudle.musicSearch.bean.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.SearchSongSheetWrapper2

data class SearchSongSheetWrapper1(
        @JSONField(name = "playlist_info")
        val searchSongSheetWrapper2: SearchSongSheetWrapper2,
        @JSONField(name = "syn_words")
        val synWords:String?
)