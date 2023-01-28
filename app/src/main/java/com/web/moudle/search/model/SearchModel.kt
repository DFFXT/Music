package com.web.moudle.search.model

import com.web.common.util.ResUtil
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.DataTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.search.bean.SearchResItem
import com.web.web.R
import io.reactivex.Observable
import org.litepal.crud.DataSupport
import java.util.*

/**
 * search model 在此处对数据进行变换
 */
class SearchModel:BaseRetrofit() {
    private val data=LinkedList<SearchResItem>()
    fun getSearchSug(word: String): Observable<List<SearchResItem>> {
        return obtainClass(NetApis.Music::class.java)
                .searchSug(word)
                .compose(SchedulerTransform())
                .compose(DataTransform())
                .map {res->
                    val data=LinkedList<SearchResItem>()
                    res.musicSugList.forEach {
                        data.add(SearchResItem(it.songName,it.songId,it.artistName,SearchResItem.SearchItemType_Music))
                    }
                    res.albumList.forEach {
                        data.add(SearchResItem(it.albumName,it.albumId,it.artistName,SearchResItem.SearchItemType_Album))
                    }
                    res.artistList.forEach {
                        data.add(SearchResItem(it.artistName,it.uid,"",SearchResItem.SearchItemType_Artist))
                    }
                    return@map data
                }
    }
    fun defSearch(time:Long):Observable<List<SearchResItem>>{
        return obtainClass(NetApis.Music::class.java)
                .defSearch(time)
                .compose(SchedulerTransform())
                .compose(DataTransform())
                .map {res->
                    data.clear()
                    res.recommendSug.recommendSongs.addAll(0,res.hotSearchSug.songList)
                    res.recommendSug.recommendSongs.forEach {
                        data.add(SearchResItem(it.songName,it.songId,it.artistName,SearchResItem.SearchItemType_Music))
                    }
                    res.hotSearchSug.playList.forEach {
                        data.add(SearchResItem(it.songSheetName,it.songSheetId,"",SearchResItem.SearchItemType_Sheet))
                    }
                    val list=LinkedList<SearchResItem>()
                    list.addAll(data)
                    list.addAll(combineHistory())
                    return@map list
                }
    }
    fun refreshHistory():Observable<List<SearchResItem>> {
        return Observable.create {
            val list=LinkedList<SearchResItem>()
            list.addAll(data)
            list.addAll(combineHistory())
            it.onNext(list)
        }
    }
    private fun combineHistory():List<SearchResItem>{
        val history=DataSupport.findAll<SearchResItem>(SearchResItem::class.java)
        val list=LinkedList<SearchResItem>()
        if(history.size!=0){
            list.add(0,SearchResItem(ResUtil.getString(R.string.searchView_history),"","",SearchResItem.SearchItemType_Head))
        }
        for(i in history.size-1 downTo 0){
            list.add(history[i])
        }
        return list
    }

}