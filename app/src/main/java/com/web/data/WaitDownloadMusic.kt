package com.web.data

import org.litepal.crud.DataSupport

class WaitDownloadMusic(var songId:String,
                        var musicName:String,
                        var singerName:String,
                        var songLink:String,
                        var lrcLink:String?=null,
                        var iconLink:String?=null,
                        var format:String?="mp3"):DataSupport() {
    var id:Int=-1
    var hasDownload:Int=0
    var fullSize:Int=0
}