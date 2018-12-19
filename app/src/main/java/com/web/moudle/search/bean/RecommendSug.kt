package com.web.moudle.search.bean

import com.alibaba.fastjson.annotation.JSONField

data class RecommendSug (
        @JSONField(name = "song")
        val recommendSongs:ArrayList<MusicSug>
)