package com.web.moudle.albumEntry.model

import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable

class AlbumEntryModel:BaseRetrofit() {
    fun getAlbumInfo(albumId:Long):Observable<AlbumResponse>{
        return obtainClass(NetApis.AlbumEntry::class.java)
                .getAlbumInfo(albumId)
                .compose(SchedulerTransform())
    }
    fun getMusicDetail(songId:String):Observable<MusicDetailInfo>{
        return obtainClass(NetApis.SongEntry::class.java)
                .getMusicDetail(songId)
                .compose(SchedulerTransform())
    }
}