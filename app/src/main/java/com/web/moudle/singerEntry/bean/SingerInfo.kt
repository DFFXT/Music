package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class SingerInfo (
        @JSONField(name = "ting_uid")
        val  uid:String,
        @JSONField(name = "avatar_s500")
        val  avatar500:String?=null,
        @JSONField(name = "listen_num")
        val  listenNum:String,
        @JSONField(name = "collect_num")
        val  collectNum:Long,
        @JSONField(name = "hot")
        val  hot:String,
        @JSONField(name = "avatar_s180")
        val  avatar180:String?=null,
        @JSONField(name = "avatar_small")
        val  avatar48:String?=null,
        @JSONField(name = "albums_total")
        val  albumTotal:String,
        @JSONField(name = "artist_id")
        val  artistId:String,
        @JSONField(name = "constellation")
        val  constellation:String,
        @JSONField(name = "intro")
        val introduction:String,
        @JSONField(name = "company")
        val company:String,
        @JSONField(name = "country")
        val country:String,
        @JSONField(name = "share_num")
        val shareNum:Long,
        @JSONField(name = "avatar_middle")
        val avatar120:String?=null,
        @JSONField(name = "mv_total")
        val totalMv:Int,
        @JSONField(name = "songs_total")
        val totalSongs:String,
        @JSONField(name = "birth")
        val birth:String,
        @JSONField(name = "avatar_s1000")
        val avatar1000:String?=null,
        @JSONField(name = "name")
        val name:String
)