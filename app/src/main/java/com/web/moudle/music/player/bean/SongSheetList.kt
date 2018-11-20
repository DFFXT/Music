package com.web.moudle.music.player.bean

import com.web.moudle.music.player.SongSheetManager
import com.web.moudle.music.player.SongSheetManager.basePath
import java.io.File
import java.io.Serializable

class SongSheetList:Serializable,DiskObject {

    override var path:String=basePath+File.separator+"songSheetList"
    var songList=ArrayList<SongSheet>()
    fun addSongSheet(songSheet: SongSheet){
        val path=basePath+File.separator+"songSheet-"+songSheet.name
        songList.forEach {
            if(it.path==path)return@forEach
        }
        val songSheetInfo=SongSheetInfo(songSheet.name)
        songSheetInfo.path=path
        songSheet.path=songSheetInfo.path
        songList.add(songSheet)
        songSheet.save()
        save()
    }
    fun removeSongSheet(index:Int){
        songList.removeAt(index)
        save()
    }
    fun getSongSheet(info:SongSheetInfo):SongSheet?{
        songList.forEach {
            if(it.path==info.path)return it
        }
        return null
    }
    fun save(){
        SongSheetManager.saveObject(this)
    }
}