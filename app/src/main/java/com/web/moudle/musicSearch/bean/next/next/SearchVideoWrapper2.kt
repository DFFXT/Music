package com.web.moudle.musicSearch.bean.next.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.SimpleVideoInfo

data class SearchVideoWrapper2 (
        @JSONField(name = "video_list")
        val videoList:ArrayList<SimpleVideoInfo> ?=ArrayList(),
        @JSONField(name = "total")
        val total:Int
)