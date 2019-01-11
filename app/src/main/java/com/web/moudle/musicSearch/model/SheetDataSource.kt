package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseSingleObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SimpleArtistInfo
import com.web.moudle.musicSearch.bean.SimpleSongSheet

class SheetDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Throwable>>):PageKeyedDataSource<String,SimpleSongSheet> (){
    var keyword:String?=null
    var page:Int=1
    private val wrapper=LiveDataWrapper<Throwable>()
    private val model=InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleSongSheet>) {
        keyword?.let {
            model.getSheetList(it,page).subscribe(object :BaseSingleObserver<ArrayList<SimpleSongSheet>>(){
                override fun onSuccess(t: ArrayList<SimpleSongSheet>) {
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleSongSheet>) {
        keyword?.let {
            page++
            model.getSheetList(it,page).subscribe(object :BaseSingleObserver<ArrayList<SimpleSongSheet>>(){
                override fun onSuccess(t: ArrayList<SimpleSongSheet>) {
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleSongSheet>) {

    }
}