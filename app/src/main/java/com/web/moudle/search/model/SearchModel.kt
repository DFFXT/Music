package com.web.moudle.search.model

import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.search.bean.DefSearchRes
import com.web.moudle.search.bean.SearchSug
import io.reactivex.Observable

class SearchModel:BaseRetrofit() {
    fun getSearchSug(word: String): Observable<SearchSug> {
        return obtainClass(NetApis.Music::class.java)
                .searchSug(word)
                .compose(SchedulerTransform())
                .compose(DataTransform())
    }
    fun defSearch(time:Long):Observable<DefSearchRes>{
        return obtainClass(NetApis.Music::class.java)
                .defSearch(time)
                .compose(SchedulerTransform())
                .compose(DataTransform())
    }

}