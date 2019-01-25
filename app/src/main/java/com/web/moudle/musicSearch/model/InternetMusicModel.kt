package com.web.moudle.musicSearch.model

import com.alibaba.fastjson.JSON
import com.web.data.InternetMusicDetailList
import com.web.data.SearchResultBd
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchAlbumWrapper1
import com.web.moudle.musicSearch.bean.next.SearchArtistWrapper1
import com.web.moudle.musicSearch.bean.next.SearchMusicWrapper1
import com.web.moudle.musicSearch.bean.next.SearchSongSheetWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import java.lang.Exception

class InternetMusicModel : BaseRetrofit() {

    fun search(keyword: String){

    }


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
    fun getSimpleMusic(keyword:String,page:Int):Observable<SearchWrapper0<SearchMusicWrapper1>>{
        return obtainClass(NetApis.Music::class.java)
                .musicSearch(keyword,20,page)
    }

    //**搜索歌手信息

    fun getArtistList(keyword:String,page:Int):Observable<SearchWrapper0<SearchArtistWrapper1>>{
        return obtainClass(NetApis.Music::class.java)
                .artistSearch(keyword,20,page)
    }

    /**
     * 获取专辑信息
     */
    fun getAlbumList(keyword: String,page: Int):Observable<SearchWrapper0<SearchAlbumWrapper1>>{
        return obtainClass(NetApis.Music::class.java)
                .albumSearch(keyword,20,page)
    }

    fun getSheetList(keyword: String,page: Int):Observable<SearchWrapper0<SearchSongSheetWrapper1>>{
        return obtainClass(NetApis.Music::class.java)
                .songSheetSearch(keyword,20,page)
    }
}