package com.web.moudle.musicSearch.bean.next.next.next

import com.alibaba.fastjson.annotation.JSONField

/**
 * 搜索的歌手信息
 */
data class SimpleArtistInfo (
        @JSONField(name = "artist_id")
        val artistId:String,
        @JSONField(name = "author")
        val artistName:String,
        @JSONField(name = "avatar_middle")
        val artistImage:String?,
        @JSONField(name = "song_num")
        val singleMusicNum:String,
        @JSONField(name = "album_num")
        val albumNum:String,
        @JSONField(name = "country")
        val district:String?,//**地区
        @JSONField(name = "artist_desc")
        val artistDescribe:String?
){
        fun stdArtistName():String{
                return artistName.replace("<em>","").replace("</em>","")
        }
}