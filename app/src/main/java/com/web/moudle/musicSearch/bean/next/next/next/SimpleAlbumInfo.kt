package com.web.moudle.musicSearch.bean.next.next.next

import com.alibaba.fastjson.annotation.JSONField

data class SimpleAlbumInfo (
        @JSONField(name = "title")
        val albumName:String,
        @JSONField(name = "album_id")
        val albumId:String,
        @JSONField(name = "pic_small")
        val albumImage:String?,
        @JSONField(name = "author")
        val artistName:String,
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "publishtime")
        val publishTime:String?,
        @JSONField(name = "company")
        val company:String,
        @JSONField(name = "hot")
        val hot:Int
){
        fun stdAlbumName():String{
                return albumName.replace("<em>","").replace("</em>","")
        }
        fun stdArtistName():String{
                return artistName.replace("<em>","").replace("</em>","")
        }
}