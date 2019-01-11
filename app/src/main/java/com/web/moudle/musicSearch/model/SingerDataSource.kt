package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseSingleObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SimpleArtistInfo

class SingerDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Throwable>>):PageKeyedDataSource<String,SimpleArtistInfo> (){
    var keyword:String?=null
    var page:Int=1
    private val wrapper=LiveDataWrapper<Throwable>()
    private val model=InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleArtistInfo>) {
        keyword?.let {
            model.getArtistList(it,page).subscribe(object :BaseSingleObserver<ArrayList<SimpleArtistInfo>>(){
                override fun onSuccess(t: ArrayList<SimpleArtistInfo>) {
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleArtistInfo>) {
        keyword?.let {
            page++
            model.getArtistList(it,page).subscribe(object :BaseSingleObserver<ArrayList<SimpleArtistInfo>>(){
                override fun onSuccess(t: ArrayList<SimpleArtistInfo>) {
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleArtistInfo>) {

    }
}