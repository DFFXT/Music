package com.web.moudle.net

import com.web.moudle.music.model.bean.RowMusicData
import com.web.moudle.net.baseBean.BaseNetBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

class NetApis {
    interface Music{
        //@GET("http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp&keyword={keyword}&page={page}")

        @GET("http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp")
        fun search(@Query("keyword") keyword:String, @Query("page") page:Int):Observable<BaseNetBean<RowMusicData>>
    }
}