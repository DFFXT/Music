package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SearchWrapper0
import com.web.moudle.musicSearch.bean.next.SearchVideoWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleVideoInfo

class VideoDataSource(private val liveData:MutableLiveData<LiveDataWrapper<Int>>):PageKeyedDataSource<String, SimpleVideoInfo> (){
    var keyword:String?=null
        set(value){
            field=value
            page=1
        }
    var page:Int=1
    private var total=0
    private val wrapper=LiveDataWrapper<Int>()
    private val model=InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleVideoInfo>) {
        load {
            callback.onResult(it,"","")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleVideoInfo>) {
        load {
            callback.onResult(it,"")
        }
    }
    private fun load(callback:((List<SimpleVideoInfo>)->Unit)){
        keyword?.let {
            model.getVideoList(it,page).subscribe(object :BaseObserver<SearchVideoWrapper1>(){
                override fun onNext(t: SearchVideoWrapper1) {
                    page++
                    val list=t.searchVideoWrapper2.videoList
                    if(list.size==0&&t.searchVideoWrapper2.total!=0){
                        wrapper.code=LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    }else{
                        callback.invoke(list)
                        if(total!=t.searchVideoWrapper2.total){
                            total=t.searchVideoWrapper2.total
                            wrapper.code=LiveDataWrapper.CODE_OK
                            wrapper.value=total
                            liveData.postValue(wrapper)
                        }
                    }
                }

                override fun error(e: Throwable) {
                    e.printStackTrace()
                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleVideoInfo>) {

    }
}