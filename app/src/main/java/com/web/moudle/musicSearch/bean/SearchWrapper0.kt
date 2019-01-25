package com.web.moudle.musicSearch.bean

import com.alibaba.fastjson.annotation.JSONField

data class SearchWrapper0<T>(
        @JSONField(name = "error_code")
        val code:Int,
        @JSONField(name = "result")
        val result:T
)