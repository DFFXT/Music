package com.web.moudle.albumEntry.bean

import com.alibaba.fastjson.annotation.JSONField

/**
 * 专辑信息
 */
data class AlbumInfo (
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "artist_ting_uid")
        val uid:String="",
        @JSONField(name = "author")
        val artistName:String,
        @JSONField(name = "avatar_small")
        val avatarSmall:String,
        @JSONField(name = "collect_num")
        val collectionNum:Long,
        @JSONField(name = "comment_num")
        val commentNum:Long,
        @JSONField(name = "country")
        val country:String,
        @JSONField(name = "file_duration")
        val duration:String?=null,//***(null)
        @JSONField(name = "hot")
        val hot:String,
        @JSONField(name = "info")
        val info:String,
        @JSONField(name = "language")
        val language:String,
        @JSONField(name = "listen_num")
        val listenNum:String,
        @JSONField(name = "pic_big")
        val pic150:String,
        @JSONField(name = "pic_radio")
        val pic300:String,
        @JSONField(name = "pic_s1000")
        val pic1000:String,
        @JSONField(name = "pic_s500")
        val pic500:String,
        @JSONField(name = "pic_small")
        val picSmall:String,
        @JSONField(name = "publishcompany")
        val publishCompany:String,
        @JSONField(name = "publishtime")
        val publishTime:String,
        @JSONField(name = "share_num")
        val shareNum:Long,
        @JSONField(name = "songs_total")
        val totalSongs:String,
        @JSONField(name = "styles")
        val styles:String,
        @JSONField(name = "title")
        val albumName:String
)