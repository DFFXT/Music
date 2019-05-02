package com.web.moudle.music.player.model

import com.web.common.base.get
import com.web.moudle.music.player.bean.LikeMusicWW
import com.web.moudle.music.player.bean.SongSheetWW
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.user.UserManager
import io.reactivex.Observable

object WWSongSheetModel:BaseRetrofit() {

    fun createSongSheet(sheetName:String,callback:((SongSheetWW)->Unit)){
        obtainClass(NetApis.UserSongSheet::class.java)
                .createSongSheet(sheetName,UserManager.getUserId(),"")
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })
    }

    fun deleteSongSheet(sheetId: Long,callback: (SongSheetWW) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .deleteSongSheet(UserManager.getUserId(),sheetId)
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })


    }
    fun getSongSheetList(callback: (List<SongSheetWW>) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .getSongSheetList(UserManager.getUserId())
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })
    }
    fun getSongSheetInfo(sheetId:Long,callback:((SongSheetWW)->Unit)){
        obtainClass(NetApis.UserSongSheet::class.java)
                .getSongListInfo(sheetId,UserManager.getUserId())
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })

    }

    fun addSongToSheet(sheetId:Long,songId:Long,name:String,artist:String,album:String,callback: (SongSheetWW) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .addSongToSheet(sheetId,songId,name,artist,
                        album,UserManager.getUserId())
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })

    }

    fun deleteFromSheet(sheetId: Long,songId: Long,callback: (SongSheetWW) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .deleteMusicFromSheet(sheetId,songId)
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })
    }

    fun getLikeList(callback: (LikeMusicWW) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .getLikeList(UserManager.getUserId())
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })
    }

    fun setAsLike(songId: Long,callback: (SongSheetWW) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .setAsLike(UserManager.getUserId(),songId)
                .compose(SchedulerTransform())
                .get({
                    callback(it)
                })
    }

    fun removeAsLike(songId: Long,callback: (Boolean) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .removeAsLike(UserManager.getUserId(),songId)
                .compose(SchedulerTransform())
                .get({
                    callback(it.code==200)
                })
    }

    fun isLikeMusic(songId: Long,callback: (Boolean) -> Unit){
        obtainClass(NetApis.UserSongSheet::class.java)
                .isLikeMusic(UserManager.getUserId(),songId)
                .compose(SchedulerTransform())
                .get({
                    callback(it.code==200)
                })
    }
}