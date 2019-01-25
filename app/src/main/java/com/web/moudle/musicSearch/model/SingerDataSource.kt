package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.base.BaseSingleObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchArtistWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo

class SingerDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Throwable>>):PageKeyedDataSource<String, SimpleArtistInfo> (){
    var keyword:String?=null
    var page:Int=1
    private val wrapper=LiveDataWrapper<Throwable>()
    private val model=InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleArtistInfo>) {
        keyword?.let {
            model.getArtistList(it,page).subscribe(object :BaseObserver<SearchWrapper0<SearchArtistWrapper1>>(){
                override fun onNext(t: SearchWrapper0<SearchArtistWrapper1>) {
                    val list=t.result.searchArtistWrapper2.artistList
                    if(list.size==0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }else{
                        callback.onResult(list,"","")
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    e.printStackTrace()
                }
            })
        }

    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleArtistInfo>) {
        keyword?.let {
            page++
            model.getArtistList(it,page).subscribe(object :BaseObserver<SearchWrapper0<SearchArtistWrapper1>>(){
                override fun onNext(t: SearchWrapper0<SearchArtistWrapper1>) {

                    val list=t.result.searchArtistWrapper2.artistList
                    if(list.size==0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }else{
                        callback.onResult(list,"")
                    }
                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleArtistInfo>) {

    }
}