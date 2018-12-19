package com.web.moudle.singerEntry.model

import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.singerEntry.bean.AlbumEntryBox
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.bean.SongEntryBox
import io.reactivex.Observable

class SingerEntryModel:BaseRetrofit() {
    fun getArtistInfo(uid:String):Observable<SingerInfo>{
        return obtainClass(NetApis.SingerEntry::class.java)
                .getArtistInfo(uid)
                .compose(SchedulerTransform())
    }
    fun getSongList(uid:String,offset:Int,limit:Int):Observable<SongEntryBox>{
        return obtainClass(NetApis.SingerEntry::class.java)
                .getSongList(uid,offset,limit)
                .compose(SchedulerTransform())
    }

    fun getAlbumList(uid:String,offset:Int,limit:Int):Observable<AlbumEntryBox>{
        return obtainClass(NetApis.SingerEntry::class.java)
                .getAlbumList(uid,offset,limit)
                .compose(SchedulerTransform())
    }
}