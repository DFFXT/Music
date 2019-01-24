package com.web.moudle.songSheetEntry.model

import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.songSheetEntry.adapter.JSEngine
import com.web.moudle.songSheetEntry.bean.SongSheetInfo
import io.reactivex.Observable

class SongSheetModel:BaseRetrofit() {

    private val jsEngine=JSEngine()
    fun getSongSheetInfo(songSheetId:String,page:Int): Observable<SongSheetInfo> {
        val request=jsEngine.getSongSheetInfo(songSheetId,page)
        return obtainClass(NetApis.SongSheetEntry::class.java)
                .getSongSheetList(request.timestamp,request.param,request.sign)
                .compose(SchedulerTransform())
    }
}