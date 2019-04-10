package com.web.moudle.music.player.bean

class SongSheet(var name: String):DiskObject {
    override var path=""
    private var musicIdList=ArrayList<Int>()
    fun size():Int{
        return musicIdList.size
    }
    fun add(musicId:Int){
        if(musicIdList.contains(musicId)) return
        musicIdList.add(musicId)
        save()
    }
    fun contains(musicId: Int):Boolean{
        return musicIdList.contains(musicId)
    }

    fun remove(musicId: Int){
        musicIdList.remove(musicId)
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