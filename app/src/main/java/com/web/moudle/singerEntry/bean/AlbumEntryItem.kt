package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class AlbumEntryItem (
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "artist_ting_uid")
        val uid:String="",
        @JSONField(name = "author")
        val artistName:String,
        @JSONField(name = "country")
        val country:String,
        @JSONField(name = "hot")
        val hot:String,
        @JSONField(name = "info")
        val info:String?=null,
        @JSONField(name = "language")
        val language:String,
        @JSONField(name = "pic_big")
        val pic150:String,
        @JSONField(name = "pic_radio")
        val pic300:String,
        @JSONField(name = "pic_small")
        val pic90:String,
        @JSONField(name = "publishcompany")
        val publishCompany:String,
        @JSONField(name = "publishtime")
        val publishTime:String,
        @JSONField(name = "songs_total")
        val songsTotal:String,
        @JSONField(name = "styles")
        val styles:String,
        @JSONField(name = "title")
        val albumName:String
)