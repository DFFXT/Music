package com.web.moudle.musicSearch.bean.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.SearchArtistWrapper2

data class SearchArtistWrapper1(
        @JSONField(name = "artist_info")
        val searchArtistWrapper2: SearchArtistWrapper2,
        @JSONField(name = "syn_words")
        val synWords:String?
)