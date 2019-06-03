package com.web.moudle.musicSearch.model

import com.web.data.InternetMusicDetailList
import com.web.data.SearchResultBd
import com.web.moudle.musicSearch.bean.next.*
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import com.web.moudle.net.retrofit.ResultTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable

class InternetMusicModel : BaseRetrofit() {

    var pageSize=30


    fun search(keyword: String, page: Int, pageSize: Int): Observable<SearchResultBd> {
        return obtainClass(NetApis.Music::class.java)
                //.search(keyword,page)
                .searchBd(keyword, pageSize, page)
        //.compose(SchedulerTransform())
        //.compose(DataTransform())

    }

    fun getMusicDetailAsync(songIds: String): Observable<InternetMusicDetailList> {
        return obtainClass(NetApis.Music::class.java)
                .musicInfo(songIds)
                .compose(SchedulerTransform())
                .compose(DataTransform())
    }
    fun getMusicDetail(songIds: String): Observable<InternetMusicDetailList> {
        return obtainClass(NetApis.Music::class.java)
                .musicInfo(songIds)
                .compose(DataTransform())
    }

    //**搜索歌曲简单信息
    fun getSimpleMusic(keyword:String,page:Int):Observable<SearchMusicWrapper1>{
        return obtainClass(NetApis.Music::class.java)
                .musicSearch(keyword,pageSize,page)
                .compose(ResultTransform())
    }
    //**搜索歌手信息
    fun getArtistList(keyword:String,page:Int):Observable<SearchArtistWrapper1>{
        return obtainClass(NetApis.Music::class.java)
                .artistSearch(keyword,pageSize,page)
                .compose(ResultTransform())
    }
     //**获取专辑信息
    fun getAlbumList(keyword: String,page: Int):Observable<SearchAlbumWrapper1>{
        return obtainClass(NetApis.Music::class.java)
                .albumSearch(keyword,pageSize,page)
                .compose(ResultTransform())
    }

    /**
     * 获取歌单信息
     */
    fun getSheetList(keyword: String,page: Int):Observable<SearchSongSheetWrapper1>{
        return obtainClass(NetApis.Music::class.java)
                .songSheetSearch(keyword,pageSize,page)
                .compose(ResultTransform())
    }
    /**
     * 获取视频信息
     */
    fun getVideoList(keyword: String,page: Int):Observable<SearchVideoWrapper1>{
        return obtainClass(NetApis.Music::class.java)
                .videoSearch(keyword,pageSize,page)
                .compose(ResultTransform())
    }
}