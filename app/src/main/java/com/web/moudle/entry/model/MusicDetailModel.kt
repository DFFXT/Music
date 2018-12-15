package com.web.moudle.entry.model

import com.web.common.base.BaseSingleObserver
import com.web.common.base.MyApplication
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.moudle.entry.bean.MusicDetailInfo
import com.web.moudle.music.page.lyrics.model.LyricsLine
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class MusicDetailModel:BaseRetrofit() {
    fun getMusicDetail(songId:String):Observable<MusicDetailInfo>{
        return obtainClass(NetApis.Entry::class.java)
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