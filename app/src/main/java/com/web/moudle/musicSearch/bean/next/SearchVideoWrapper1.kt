package com.web.moudle.musicSearch.bean.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.SearchVideoWrapper2

data class SearchVideoWrapper1(
        @JSONField(name = "video_info")
        val searchVideoWrapper2:SearchVideoWrapper2,
        @JSONField(name = "syn_words")
        val synWords:String?
)