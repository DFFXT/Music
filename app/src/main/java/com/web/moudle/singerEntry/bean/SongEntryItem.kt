package com.web.moudle.singerEntry.bean

import com.alibaba.fastjson.annotation.JSONField

data class SongEntryItem(
        @JSONField(name = "title")
        val title:String,
        @JSONField(name = "country")
        val country:String,
        @JSONField(name = "ting_uid")
        val uid:String,
        @JSONField(name = "si_proxycompany")
        val proxyCompany:String,
        @JSONField(name = "album_1000_1000")
        val album1000:String?=null,
        @JSONField(name = "album_500_500")
        val album500:String?=null,
        @JSONField(name = "album_800_800")
        val album800:String?=null,
        @JSONField(name = "pic_big")
        val picBig:String,
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "album_title")
        val albumName:String,
        @JSONField(name = "song_id")
        val songId:String,
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "author")
        val author:String,
        @JSONField(name = "publishtime")
        val publishTime:String,
        @JSONField(name = "file_duration")
        val duration:String,
        @JSONField(name = "pic_radio")
        val picRadio:String,
        @JSONField(name = "lrclink")
        val lrcLink:String,
        @JSONField(name = "pic_small")
        val picSmall:String,
        @JSONField(name = "pic_premium")
        val picPremium:String,
        @JSONField(name = "language")
        val language:String,
        @JSONField(name = "all_rate")
        val allRate:String
)