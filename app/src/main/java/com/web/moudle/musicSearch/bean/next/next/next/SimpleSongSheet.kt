package com.web.moudle.musicSearch.bean.next.next.next

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.musicSearch.bean.next.next.next.next.UserInfo

data class SimpleSongSheet(
        @JSONField(name = "diy_id")
        val sheetId:String,
        @JSONField(name = "diy_title")
        val sheetName:String,
        @JSONField(name = "diy_pic")
        val sheetIcon:String?,
        @JSONField(name = "userinfo")
        val userInfo:UserInfo,
        @JSONField(name = "song_num")
        val songCount:String,
        @JSONField(name = "listen_num")
        val playCount:String
){
        fun stdSheetName():String{
                return sheetName.replace("<em>","").replace("</em>","")
        }
}