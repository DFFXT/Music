package com.web.moudle.musicSearch.bean.next.next.next

import com.alibaba.fastjson.annotation.JSONField

/**
 * 搜索音乐简单信息
 */
data class SimpleMusicInfo(
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "album_title")
        val albumTitle:String,
        @JSONField(name = "all_artist_id")
        val allArtistId:String,
        @JSONField(name = "all_rate")
        val allRate:String,
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "author")
        val author:String,
        @JSONField(name = "biaoshi")
        val tag:String, //**"vip,lossless,perm-1" vip和无损
        @JSONField(name = "has_filmtv")
        val hasFilmTv:String,
        @JSONField(name = "has_mv")
        val hasMV:Int,
        @JSONField(name = "lrclink")
        val lrcLink:String,
        @JSONField(name = "pic_small")
        val picSmall:String?,
        @JSONField(name = "si_proxycompany")
        val siProxyCompany:String?,
        @JSONField(name = "song_id")
        val songId:String,
        @JSONField(name = "ting_uid")
        val uid:String,

        @JSONField(name = "title")
        val musicName:String,

        @JSONField(name = "album_image_url")
        val albumImage:String?=null



)
/*
song_id : "607851549"
song_title : "我想你了"
artist_id : "5418132"
song_artist : "李雨儿"
album_id : "607851547"
album_title : "我想你了"
album_image_url : "http://qukufile2.qianqian.com/data2/pic/ad216f76c5b726a6b19e08200c328752/607894735/607894735.jpg@s_1,w_90,h_90"
album_relate_status : 1
copy_type : "1"
has_mv : 0
biaoshi : "vip,lossless,perm-1"
 */