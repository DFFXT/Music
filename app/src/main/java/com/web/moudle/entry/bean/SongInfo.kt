package com.web.moudle.entry.bean

import com.alibaba.fastjson.annotation.JSONField
import com.web.moudle.search.bean.AlbumSug

data class SongInfo (
        @JSONField(name = "hot")
        val hot:Int,
        @JSONField(name = "title")
        val title:String,
        @JSONField(name = "country")
        val country:String,
        @JSONField(name = "ting_uid")
        val uid:String,
        @JSONField(name = "expire")
        val expire:Int,
        @JSONField(name = "si_proxycompany")
        val proxyCompany:String,
        @JSONField(name = "compose")
        val compose:String,
        @JSONField(name = "artist_640_1136")
        val artistPic640x1136:String,
        @JSONField(name = "artist_500_500")
        val artistPic500x500:String,
        @JSONField(name = "artist_480_800")
        val artistPic480x800:String,
        @JSONField(name = "pic_big")
        val picBig:String,
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "album_title")
        val albumName:String,
        @JSONField(name = "total_listen_nums")
        val listenTimes:String,
        @JSONField(name = "song_id")
        val songId:String,
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "artist")
        val artistName:String,
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
        val picPremium:String

)