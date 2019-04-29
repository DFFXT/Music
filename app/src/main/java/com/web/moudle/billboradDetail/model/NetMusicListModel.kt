package com.web.moudle.billboradDetail.model

import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.billboradDetail.bean.RecommendMusicBox
import com.web.moudle.net.NetApis
import com.web.moudle.net.baseBean.BaseNetBean
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.singerEntry.bean.AlbumEntryBox
import com.web.moudle.singerEntry.bean.SongEntryBox
import io.reactivex.Observable

class NetMusicListModel:BaseRetrofit() {

    fun requestList(type:Int,offset:Int,size:Int):Observable<NetMusicBox>{
        return obtainClass(NetApis.HomePage::class.java)
                .requestList(type,offset,size)
    }
    fun requestRecommend(page:Int,pageSize:Int):Observable<BaseNetBean<RecommendMusicBox>>{
        return obtainClass(NetApis.HomePage::class.java)
                .requestRecommend(page,pageSize)
    }

    fun requestSingerAllMusic(uid:String,offset:Int,limits:Int):Observable<SongEntryBox>{
        return obtainClass(NetApis.SingerEntry::class.java)
                .getSongList(uid,offset,limits)
    }

    fun requestSingerAllAlbum(uid:String,offset:Int,limits:Int):Observable<AlbumEntryBox>{
        return obtainClass(NetApis.SingerEntry::class.java)
                .getAlbumList(uid,offset,limits)
    }

}