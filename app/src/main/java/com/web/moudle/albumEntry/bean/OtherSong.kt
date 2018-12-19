package com.web.moudle.albumEntry.bean

import com.alibaba.fastjson.annotation.JSONField

/**
 * 其他音乐
 */
data class OtherSong (
        @JSONField(name = "album_1000_1000")
        val album1000:String,
        @JSONField(name = "album_500_500")
        val album500:String,
        @JSONField(name = "album_800_800")
        val album800:String?=null,
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "album_title")
        val albumName:String,
        @JSONField(name = "artist_id")
        val artistId:Long,
        @JSONField(name = "author")
        val author:String,
        @JSONField(name = "country")
        val country:String,
        @JSONField(name = "file_duration")
        val duration:String,
        @JSONField(name = "has_mv")
        val hasMv:Int,
        @JSONField(name = "has_mv_mobile")
        val hasMobileMv:Int,
        @JSONField(name = "hot")
        val hot:String,
        @JSONField(name = "info")
        val info:String,
        @JSONField(name = "language")
        val language:String,
        @JSONField(name = "lrclink")
        val lrcLink:String,
        @JSONField(name = "pic_big")
        val pic150:String,
        @JSONField(name = "pic_huge")
        val pic1000:String,
        @JSONField(name = "pic_s500")
        val pic500:String,
        @JSONField(name = "pic_radio")
        val pic300:String,
        @JSONField(name = "pic_small")
        val pic90:String,
        @JSONField(name = "publishtime")
        val publishTime:String,
        @JSONField(name = "si_proxycompany")
        val publishCompany:String,
        @JSONField(name = "song_id")
        val songId:String,
        @JSONField(name = "ting_uid")
        val uid:String,
        @JSONField(name = "title")
        val title:String,
        @JSONField(name = "versions")
        val version:String,
        @JSONField(name = "all_rate")
        val allRate:String


)