package com.web.moudle.music.player

import com.web.common.base.MyApplication
import com.web.common.base.get
import com.web.common.util.IOUtil
import com.web.data.Music
import com.web.moudle.music.player.bean.DiskObject
import com.web.moudle.music.player.bean.SongSheet
import com.web.moudle.music.player.bean.SongSheetList
import com.web.moudle.music.player.bean.SongSheetWW
import com.web.moudle.music.player.model.WWSongSheetModel
import com.web.moudle.user.UserManager
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.ObjectInputStream

object SongSheetManager {
    private var songSheetList:SongSheetList?=null

    private val model=WWSongSheetModel

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
            saveObject(songSheetList!!)
        }

        return songSheetList!!
    }
    fun createNewSongSheet(sheetName:String,callback:((SongSheetWW)->Unit)?=null){
        val songSheet= SongSheet(sheetName)
        getSongSheetList().addSongSheet(songSheet)
        val songSheetWW=SongSheetWW()
        songSheetWW.code=200
        songSheetWW.name=sheetName
        callback?.invoke(songSheetWW)
    }



    fun setAsLike(music: Music){
        music.isLike=true
        getSongSheetList().songList[0].add(music.id)
        music.saveOrUpdate()
        SongSheetManager.getSongSheetList().save()
    }

    fun removeLike(music: Music){
        music.isLike=false
        getSongSheetList().songList[0].remove(music.id)
        music.saveOrUpdate()
        SongSheetManager.getSongSheetList().save()
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