package com.web.moudle.musicSearch.bean

/**
 * 搜索的歌手信息
 */
data class SimpleArtistInfo (
        val artistId:String,
        val artistName:String,
        val artistImage:String,
        val singleMusicNum:String,
        val albumNum:String,
        val district:String//**地区
)