package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.base.BaseSingleObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchAlbumWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo

class AlbumDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Throwable>>):PageKeyedDataSource<String, SimpleAlbumInfo> (){
    var keyword:String?=null
    var page:Int=1
    private val model=InternetMusicModel()
    private val wrapper=LiveDataWrapper<Throwable>()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleAlbumInfo>) {
        keyword?.let {
            model.getAlbumList(it,page).subscribe(object :BaseObserver<SearchWrapper0<SearchAlbumWrapper1>>(){
                override fun onNext(res: SearchWrapper0<SearchAlbumWrapper1>) {
                    val t=res.result.searchAlbumWrapper2.albumList
                    if(t.size==0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }else{
                        callback.onResult(t,"","")
                    }
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    e.printStackTrace()
                }
            })
        }

    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleAlbumInfo>) {
        keyword?.let {
            page++
            model.getAlbumList(it,page).subscribe(object :BaseObserver<SearchWrapper0<SearchAlbumWrapper1>>(){
                override fun onNext(res: SearchWrapper0<SearchAlbumWrapper1>) {
                    val t=res.result.searchAlbumWrapper2.albumList
                    if(t.size==0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }else{
                        callback.onResult(t,"")
                    }

                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleAlbumInfo>) {

    }
}