package com.web.moudle.musicSearch.bean.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.SearchAlbumWrapper2

data class SearchAlbumWrapper1(
        @JSONField(name = "album_info")
        val searchAlbumWrapper2: SearchAlbumWrapper2,
        @JSONField(name = "syn_words")
        val synWords:String?
)