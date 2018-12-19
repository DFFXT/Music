package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

data class DefSearchRes (
        @JSONField(name = "list")
        val hotSearchSug: HotSearchSug,
        @JSONField(name = "recommend")
        val recommendSug: RecommendSug
)