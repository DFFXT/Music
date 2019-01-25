package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchVideoWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleVideoInfo

class VideoDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Throwable>>):PageKeyedDataSource<String, SimpleVideoInfo> (){
    var keyword:String?=null
    var page:Int=1
    private val wrapper=LiveDataWrapper<Throwable>()
    private val model=InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleVideoInfo>) {
        keyword?.let {
            model.getVideoList(it,page).subscribe(object : BaseObserver<SearchWrapper0<SearchVideoWrapper1>>(){

                override fun onNext(res: SearchWrapper0<SearchVideoWrapper1>) {
                    val t=res.result.searchVideoWrapper2.videoList
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleVideoInfo>) {
        keyword?.let {
            page++
            model.getVideoList(it,page).subscribe(object :BaseObserver<SearchWrapper0<SearchVideoWrapper1>>(){
                override fun onNext(res: SearchWrapper0<SearchVideoWrapper1>) {
                    val t=res.result.searchVideoWrapper2.videoList
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleVideoInfo>) {

    }
}