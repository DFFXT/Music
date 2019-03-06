package com.web.moudle.music.player

import com.web.common.base.MyApplication
import com.web.common.util.IOUtil
import com.web.moudle.music.player.bean.DiskObject
import com.web.moudle.music.player.bean.SongSheet
import com.web.moudle.music.player.bean.SongSheetList
import java.io.*

object SongSheetManager {
    private var songSheetList:SongSheetList?=null

    private var mBasePath=MyApplication.context.filesDir.absolutePath+ File.separator+"sheet"
        get(){
            val file= File(field)
            if(!file.exists()){
                file.mkdir()
            }
            return field
        }
    val basePath= mBasePath
    fun getSongSheetList():SongSheetList{
        try {
            if(songSheetList==null){
                FileInputStream(basePath+File.separator+"songSheetList").use { fis->
                    ObjectInputStream(fis).use {ois->
                        songSheetList=ois.readObject() as SongSheetList
                    }
                }
            }
        }catch (e:IOException){
            songSheetList= SongSheetList()
            SongSheetManager.saveObject(songSheetList!!)
        }

        return songSheetList!!
    }
    fun createNewSongSheet(sheetName:String){
        val songSheet= SongSheet(sheetName)
        getSongSheetList().addSongSheet(songSheet)
    }

    fun getSerializableObject(path:String):Any?{
        var any:Any?=null
        FileInputStream(path).use {fis->
            ObjectInputStream(fis).use {ois->
                any=ois.readObject()
            }
        }
        return any
    }
    fun saveObject(diskObject: DiskObject){
        IOUtil.saveObject(diskObject)
    }
}