package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.base.BaseSingleObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchArtistWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo

class ArtistDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Int>>):PageKeyedDataSource<String, SimpleArtistInfo> (){
    var keyword:String?=null
        set(value){
            field=value
            page=1
        }
    var page:Int=1
    private var total=0
    private val wrapper=LiveDataWrapper<Int>()
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
                    if(list.size==0&&t.searchArtistWrapper2.total!=0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }
                    callback.invoke(list)
                    if(total!=t.searchArtistWrapper2.total){
                        wrapper.code=LiveDataWrapper.CODE_OK
                        wrapper.value=t.searchArtistWrapper2.total
                        liveData.postValue(wrapper)
                        total=t.searchArtistWrapper2.total
                    }
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