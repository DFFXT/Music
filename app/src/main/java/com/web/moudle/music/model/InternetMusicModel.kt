package com.web.moudle.music.model

import com.web.data.InternetMusicDetailList
import com.web.data.SearchResultBd
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable

class InternetMusicModel:BaseRetrofit() {


    fun search(keyword:String,page:Int,pageSize:Int):Observable<SearchResultBd>{
        return obtainClass(NetApis.Music::class.java)
                //.search(keyword,page)
                .searchBd(keyword,pageSize,page)
                //.compose(SchedulerTransform())
                //.compose(DataTransform())

    }
    fun getMusicDetail(songIds:String):Observable<InternetMusicDetailList>{
        return obtainClass(NetApis.Music::class.java)
                .muiscInfo(songIds)
                .compose(SchedulerTransform())
                .compose(DataTransform())
    }
}