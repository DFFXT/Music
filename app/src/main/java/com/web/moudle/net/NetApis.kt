package com.web.moudle.net

import com.web.data.InternetMusicDetailList
import com.web.data.SearchResultBd
import com.web.moudle.music.model.bean.RowMusicData
import com.web.moudle.net.baseBean.BaseNetBean
import com.web.moudle.search.bean.SearchSug
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

class NetApis {
    interface Music {
        //@GET("http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp&keyword={keyword}&page={page}")

        @Deprecated("use baidu api instead")
        @GET("http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp")
        fun search(@Query("keyword") keyword: String, @Query("page") page: Int): Observable<BaseNetBean<RowMusicData>>

        @GET("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.common&format=json")
        fun searchBd(@Query("query") keyword: String, @Query("page_size") pageSize: Int, @Query("page_no") page: Int): Observable<SearchResultBd>

        @GET("http://music.taihe.com/data/music/links?")
        fun musicInfo(@Query("songIds") songIds: String): Observable<BaseNetBean<InternetMusicDetailList>>

        @GET("http://sug.qianqian.com/info/suggestion?format=json")
        fun searchSug(@Query("word") word: String): Observable<BaseNetBean<SearchSug>>
    }
}