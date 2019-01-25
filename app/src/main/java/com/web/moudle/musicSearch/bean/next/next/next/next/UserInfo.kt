package com.web.moudle.musicSearch.bean.next.next.next.next

import com.alibaba.fastjson.annotation.JSONField

data class UserInfo (
        @JSONField(name = "userpic")
        val userIcon:String?,
        @JSONField(name = "userpic_small")
        val userSmallIcon:String?,
        @JSONField(name = "userid")
        val userId:String,
        @JSONField(name = "username")
        val userName:String
)