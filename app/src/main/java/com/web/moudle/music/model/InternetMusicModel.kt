package com.web.moudle.music.model

import com.web.moudle.music.model.bean.RowMusicData
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import io.reactivex.Observable

class InternetMusicModel:BaseRetrofit() {


    fun search(keyword:String,page:Int):Observable<RowMusicData>{
        return obtainClass(NetApis.Music::class.java)
                .search(keyword,page)
                //.compose(SchedulerTransform())
                .compose(DataTransform())

    }
}