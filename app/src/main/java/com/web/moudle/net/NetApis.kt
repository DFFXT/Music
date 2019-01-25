package com.web.moudle.net

import com.web.data.InternetMusicDetailList
import com.web.data.SearchResultBd
import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.moudle.musicSearch.bean.RowMusicData
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.*
import com.web.moudle.net.baseBean.BaseNetBean
import com.web.moudle.search.bean.DefSearchRes
import com.web.moudle.search.bean.SearchSug
import com.web.moudle.singerEntry.bean.AlbumEntryBox
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.bean.SongEntryBox
import com.web.moudle.songSheetEntry.bean.SongSheetInfo
import io.reactivex.Observable
import retrofit2.Call
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

        @GET("http://sug.qianqian.com/info/suggestion?format=json&word=&version=2&from=web&third_type=0&client_type=0")
        fun defSearch(@Query("_") time: Long): Observable<BaseNetBean<DefSearchRes>>


        //**混合搜索**可以用单一搜索组合代替
        @GET("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.9.1.0&channel=ppzs&operator=0&method=baidu.ting.search.merge&format=json&page_no=1&page_size=30&type=-1&data_source=0&isNew=1&use_cluster=1")
        fun multiSearch(@Query("query") query: String): Observable<SearchWrapper0<SearchMultiWrapper1>>


        //**搜索单一歌曲
        @GET("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.9.1.0&channel=ppzs&operator=0&method=baidu.ting.search.merge&format=json&type=0&data_source=0&isNew=1&use_cluster=1")
        fun musicSearch(@Query("query") keyword: String, @Query("page_size") pageSize: Int, @Query("page_no") page: Int): Observable<SearchWrapper0<SearchMusicWrapper1>>

        //**搜索歌手
        @GET("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.9.1.0&channel=ppzs&operator=0&method=baidu.ting.search.merge&format=json&type=1&data_source=0&isNew=1&use_cluster=1")
        fun artistSearch(@Query("query") keyword: String, @Query("page_size") pageSize: Int, @Query("page_no") page: Int): Observable<SearchWrapper0<SearchArtistWrapper1>>

        //**搜索专辑
        @GET("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.9.1.0&channel=ppzs&operator=0&method=baidu.ting.search.merge&format=json&type=2&data_source=0&isNew=1&use_cluster=1")
        fun albumSearch(@Query("query") keyword: String, @Query("page_size") pageSize: Int, @Query("page_no") page: Int): Observable<SearchWrapper0<SearchAlbumWrapper1>>

        //**搜索歌单
        @GET("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.9.1.0&channel=ppzs&operator=0&method=baidu.ting.search.merge&format=json&type=10&data_source=0&isNew=1&use_cluster=1")
        fun songSheetSearch(@Query("query") keyword: String, @Query("page_size") pageSize: Int, @Query("page_no") page: Int): Observable<SearchWrapper0<SearchSongSheetWrapper1>>

        //**视频搜索
        @GET("http://musicapi.qianqian.com/v1/restserver/ting?from=android&version=6.9.1.0&channel=ppzs&operator=0&method=baidu.ting.search.merge&format=json&type=14&data_source=0&isNew=1&use_cluster=1")
        fun videoSearch(@Query("query") keyword: String, @Query("page_size") pageSize: Int, @Query("page_no") page: Int): Observable<SearchWrapper0<SearchVideoWrapper1>>

    }

    interface SongEntry {
        @GET("http://musicapi.taihe.com/v1/restserver/ting?method=baidu.ting.song.playAAC")
        fun getMusicDetail(@Query("songid") songId: String): Observable<MusicDetailInfo>
    }

    interface AlbumEntry {
        @GET("http://music.taihe.com/data/tingapi/v1/restserver/ting?method=baidu.ting.album.getAlbumInfo")
        fun getAlbumInfo(@Query("album_id") albumId: String): Observable<AlbumResponse>
    }

    interface SingerEntry {
        //@GET("http://music.taihe.com/data/tingapi/v1/restserver/ting?method=baidu.ting.artist.getInfo&artistid=90")
        @GET("http://tingapi.ting.baidu.com/v1/restserver/ting?from=qianqian&version=2.1.0&method=baidu.ting.artist.getinfo&format=json")
        fun getArtistInfo(@Query("tinguid") uid: String): Observable<SingerInfo>

        @GET("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.artist.getSongList&format=json&order=2")
        fun getSongList(@Query("tinguid") uid: String, @Query("offset") offset: Int, @Query("limits") limit: Int): Observable<SongEntryBox>

        @GET("http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.artist.getAlbumList&format=json&order=2")
        fun getAlbumList(@Query("tinguid") uid: String, @Query("offset") offset: Int, @Query("limits") limit: Int): Observable<AlbumEntryBox>

        @GET("http://music.taihe.com/data/artist/redirect")
        fun getArtistId(@Query("id") fakeId: String): Call<Any>
    }

    interface SongSheetEntry {
        //**获取歌单信息
        @GET("http://musicmini.qianqian.com/v1/restserver/ting?method=baidu.ting.ugcdiy.getBaseInfo")
        fun getSongSheetList(@Query("timestamp") timestamp: String, @Query("param") param: String, @Query("sign") sign: String): Observable<SongSheetInfo>

    }

}