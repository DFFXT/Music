package com.web.moudle.music.player.bean

class SongSheet(var name: String):DiskObject {
    override var path=""
    private var musicIdList=ArrayList<Int>()
    fun size():Int{
        return musicIdList.size
    }
    fun add(musicId:Int){
        var has=false
        musicIdList.forEach{
            if(musicId==it){
                has=true
                return@forEach
            }
        }
        if(!has){
            musicIdList.add(musicId)
        }
        save()
    }
    fun remove(musicId: Int){
        for(i in musicIdList.indices){
            if(musicId==musicIdList[i]){
                musicIdList.removeAt(i)
                break
            }
        }
        save()
    }
    fun each(block:((Int)->Unit)){
        musicIdList.forEach{
            block.invoke(it)
        }
    }
    fun save(){
        //SongSheetManager.saveObject(this)
    }
}