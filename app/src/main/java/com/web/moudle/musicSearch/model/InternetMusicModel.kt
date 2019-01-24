package com.web.moudle.musicSearch.model

import com.alibaba.fastjson.JSON
import com.web.common.base.BaseSingleObserver
import com.web.common.base.SingleSchedulerTransform
import com.web.data.InternetMusicDetailList
import com.web.data.SearchResultBd
import com.web.moudle.musicSearch.bean.SimpleAlbumInfo
import com.web.moudle.musicSearch.bean.SimpleArtistInfo
import com.web.moudle.musicSearch.bean.SimpleMusicInfo
import com.web.moudle.musicSearch.bean.SimpleSongSheet
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import retrofit2.Retrofit
import java.lang.Exception

class InternetMusicModel : BaseRetrofit() {


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
    fun getSimpleMusic(keyword:String,page:Int):Single<ArrayList<SimpleMusicInfo>>{
        return Single.create<ArrayList<SimpleMusicInfo>> { emitter->
            try {
                val list= ArrayList<SimpleMusicInfo>()
                val doc = Jsoup.connect("http://musicmini.qianqian.com/2018/app/search/searchListUgc.php?qword=$keyword&page=$page").get()
                val tbody=doc.getElementsByTag("tbody")
                if(tbody==null){
                    emitter.onSuccess(list)
                    return@create
                }
                val tr=tbody[0].getElementsByTag("tr")
                tr.forEach {
                    val json=it.attr("data-song")
                    val obj= JSON.parseObject(json,SimpleMusicInfo::class.java)
                    list.add(obj)
                }
                emitter.onSuccess(list)
            }catch (e: Exception){
                emitter.onError(e)
            }
        }//.compose(SingleSchedulerTransform())
    }

    //**搜索歌手信息

    fun getArtistList(keyword:String,page:Int):Single<ArrayList<SimpleArtistInfo>>{
        return Single.create <ArrayList<SimpleArtistInfo>>{emitter->
            try {
                val list=ArrayList<SimpleArtistInfo>()
                val ele = Jsoup.connect("http://musicmini.qianqian.com/2018/app/search/searchListUgc.php?qword=$keyword&page=$page&type=2").get()
                val divs=ele.getElementsByClass("search_artist_list")
                if(divs==null){
                    emitter.onSuccess(list)
                    return@create
                }
                val ul=divs[0].child(0)
                ul.getElementsByTag("li").forEach {li->
                    val artistId=li.attr("data-artistid")
                    val artistImage=li.child(0).child(0).attr("key")
                    val artistName=li.child(1).child(0).text()
                    val singleMusicNum=li.child(1).child(1).child(0).getElementsByTag("a")[0].text()
                    val albumNum=li.child(1).child(1).child(1).getElementsByTag("a")[0].text()
                    var district=li.child(1).child(1).child(2).text()
                    district=district.substring(3,district.length)
                    list.add(SimpleArtistInfo(artistId,artistName,artistImage,singleMusicNum,albumNum,district))
                }
                emitter.onSuccess(list)
            }catch (e:Exception){
                emitter.onError(e)
            }

        }
    }

    /**
     * 获取专辑信息
     */
    fun getAlbumList(keyword: String,page: Int):Single<ArrayList<SimpleAlbumInfo>>{
        return Single.create<ArrayList<SimpleAlbumInfo>> {emitter->
            try {
                val list=ArrayList<SimpleAlbumInfo>()
                val ele=Jsoup.connect("http://musicmini.qianqian.com/2018/app/search/searchListUgc.php?qword=$keyword&page=$page&type=3").get()
                val divs=ele.getElementsByClass("search_album_list")
                if(divs==null){
                    emitter.onSuccess(list)
                    return@create
                }
                val ul=divs[0].child(0)
                ul.getElementsByTag("li").forEach {li->
                    val albumId=li.attr("data-albumid")
                    val albumImage=li.child(0).child(0).attr("key")
                    val albumName=li.child(1).child(0).child(0).text()
                    val artistName=li.child(1).child(0).child(1).text()
                    val artistId=li.child(1).child(0).child(1).attr("data-artistid")
                    val publishTime=li.child(1).child(1).getElementsByTag("span")[0].text()
                    list.add(SimpleAlbumInfo(albumName,albumId,albumImage,artistName,artistId,publishTime))
                }
                emitter.onSuccess(list)
            }catch (e:Exception){
                emitter.onError(e)
            }
        }
    }

    fun getSheetList(keyword: String,page: Int):Single<ArrayList<SimpleSongSheet>>{
        return Single.create<ArrayList<SimpleSongSheet>> {emitter->
            try {
                val data=ArrayList<SimpleSongSheet>()
                val doc = Jsoup.connect("http://musicmini.qianqian.com/2018/app/search/searchListUgc.php?qword=$keyword&type=4").get()
                val box = doc.getElementsByClass("search_gedan_list")[0]
                val list=box.getElementsByTag("li")
                list.forEach {li->
                    val songSheetId=li.attr("data-gedanid")
                    val sheetIcon = li.getElementsByClass("img_border")[0].child(0).attr("key")
                    val contentBox = li.getElementsByClass("text_border")[0]
                    val sheetName=contentBox.child(0).child(0).text()
                    val songCount=contentBox.child(1).getElementsByClass("songnum")[0].text()
                    val creator=contentBox.child(1).getElementsByClass("gedanuser")[0].text()
                    var playCount=contentBox.child(1).getElementsByClass("listennum")[0].text()
                    playCount=playCount.substring(playCount.lastIndexOf(":")+1)
                    data.add(SimpleSongSheet(songSheetId,sheetName,sheetIcon,creator,songCount,playCount))
                }
                emitter.onSuccess(data)
            }catch (e:Exception){
                emitter.onError(e)
            }
        }
    }
}