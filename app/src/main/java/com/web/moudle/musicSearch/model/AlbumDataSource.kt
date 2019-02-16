package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.next.SearchAlbumWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo

class AlbumDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Int>>):PageKeyedDataSource<String, SimpleAlbumInfo> (){
    var keyword:String?=null
        set(value){
            field=value
            page=1
            total=0
        }
    var page:Int=1
    private var total=0
    private val model=InternetMusicModel()
    private val wrapper=LiveDataWrapper<Int>()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleAlbumInfo>) {
        load {
            callback.onResult(it,"","")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleAlbumInfo>) {
        load {
            callback.onResult(it,"")
        }
    }
    private fun load(callback:((List<SimpleAlbumInfo>)->Unit)){
        keyword?.let {
            model.getAlbumList(it,page).subscribe(object :BaseObserver<SearchAlbumWrapper1>(){
                override fun onNext(res: SearchAlbumWrapper1) {
                    page++

                    val box=res.searchAlbumWrapper2
                    if(box.total==0){
                        wrapper.code=LiveDataWrapper.CODE_OK
                        wrapper.value=0
                        liveData.postValue(wrapper)
                    }
                    else if(box.albumList == null ||box.albumList.size==0&&box.total!=0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }else{
                        callback.invoke(box.albumList)
                        if(total!=res.searchAlbumWrapper2.total){
                            wrapper.code=LiveDataWrapper.CODE_OK
                            wrapper.value=res.searchAlbumWrapper2.total
                            liveData.postValue(wrapper)
                            total=res.searchAlbumWrapper2.total
                        }
                    }

                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleAlbumInfo>) {

    }
}