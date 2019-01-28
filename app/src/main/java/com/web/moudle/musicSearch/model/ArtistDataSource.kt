package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.base.BaseSingleObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchArtistWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo

class ArtistDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Throwable>>):PageKeyedDataSource<String, SimpleArtistInfo> (){
    var keyword:String?=null
    var page:Int=1
    private val wrapper=LiveDataWrapper<Throwable>()
    private val model=InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleArtistInfo>) {
        size=0
        load {
            callback.onResult(it,"","")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleArtistInfo>) {
         load {            callback.onResult(it,"")
        }
    }
    private var size=0
    private fun load(callback:((List<SimpleArtistInfo>)->Unit)){
        keyword?.let {
            model.getArtistList(it,page).subscribe(object :BaseObserver<SearchArtistWrapper1>(){
                override fun onNext(t: SearchArtistWrapper1) {
                    page++
                    val list= t.searchArtistWrapper2.artistList ?: return
                    size+=list.size
                    if(list.size==0||size==t.searchArtistWrapper2.total){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }
                    callback.invoke(list)
                }

                override fun error(e: Throwable) {
                    e.printStackTrace()
                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleArtistInfo>) {

    }
}