package com.web.moudle.musicEntry.model

import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.moudle.lyrics.LyricsLine
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MusicDetailModel:BaseRetrofit() {
    fun getMusicDetail(songId:String):Observable<MusicDetailInfo>{
        return obtainClass(NetApis.SongEntry::class.java)
                .getMusicDetail(songId)
                .compose(SchedulerTransform())
    }
    fun getLyrics(lrcLink:String):Single<ArrayList<LyricsLine>>{
        return Single.create<ArrayList<LyricsLine>> {
            try {
                it.onSuccess(LyricsAnalysis(GetFiles.readNetData(lrcLink)).lyrics)
            }catch (e:Exception){
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}