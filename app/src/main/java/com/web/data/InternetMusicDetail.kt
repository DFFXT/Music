package com.web.data

import com.alibaba.fastjson.annotation.JSONField
import org.litepal.crud.DataSupport
import java.io.Serializable

data class InternetMusicDetail(

        @JSONField(name = "songId")
        var songId:String,
        @JSONField(name = "songName")
        var songName:String,
        @JSONField(name = "artistName")
        var artistName:String,
        var albumId:String?=null,
        var albumName:String?=null,
        @JSONField(name = "time")
        var duration:Int,
        @JSONField(name = "size")
        var size:Long,
        @JSONField(name = "lrcLink")
        var lrcLink:String?=null,
        @JSONField(name = "songLink")
        var songLink:String,
        @JSONField(name = "songPicSmall")
        var singerIconSmall:String?=null,
        var format:String
        //***自己设置
        //var itemId:Int?=-1,
        //var path:String?,
        //var hasDownload:Long?=0

):DataSupport(),Serializable{
        var id:Int=-1
        var path:String=""
        var hasDownload:Long=0
}