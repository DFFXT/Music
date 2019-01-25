package com.web.moudle.musicSearch.bean.next.next.next

import com.alibaba.fastjson.annotation.JSONField

data class SimpleVideoInfo (
        @JSONField(name = "all_artist_id")
        val allArtistId:String,
        @JSONField(name = "artist")
        val artistName:String?,
        @JSONField(name = "duration")
        val duration:String,
        @JSONField(name = "mv_id")
        val mvId:String,
        @JSONField(name = "thumbnail")
        val thumbnail:String?,
        @JSONField(name = "thumbnail2")
        val thumbnail2: String?,
        @JSONField(name = "ting_uid")
        val uid:String,
        @JSONField(name = "title")
        val title:String

){
        fun stdVideoName():String{
                return title.replace("<em>","").replace("</em>","")
        }
        fun stdVideoArtistName():String?{
                return artistName?.replace("<em>","")?.replace("</em>","")
        }
}