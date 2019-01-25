package com.web.moudle.musicSearch.bean.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.SearchSongWrapper2

data class SearchMusicWrapper1(
        @JSONField(name = "song_info")
        val searchSongWrapper2: SearchSongWrapper2,
        @JSONField(name = "syn_words")
        val synWords:String?
)