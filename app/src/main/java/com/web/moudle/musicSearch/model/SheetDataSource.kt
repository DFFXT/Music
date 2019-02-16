package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.next.SearchSongSheetWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet

class SheetDataSource(private val liveData: MutableLiveData<LiveDataWrapper<Int>>) : PageKeyedDataSource<String, SimpleSongSheet>() {
    var keyword: String? = null
        set(value){
            field=value
            page=1
            total=0
        }
    var page: Int = 1
    private var total=0
    private val wrapper = LiveDataWrapper<Int>()
    private val model = InternetMusicModel()
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleSongSheet>) {
        load {
            callback.onResult(it, "", "")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleSongSheet>) {
        load {
            callback.onResult(it, "")
        }
    }

    private fun load(callback: ((List<SimpleSongSheet>) -> Unit)) {
        keyword?.let {
            model.getSheetList(it, page).subscribe(object : BaseObserver<SearchSongSheetWrapper1>() {
                override fun onNext(res: SearchSongSheetWrapper1) {
                    page++
                    val box = res.searchSongSheetWrapper2
                    if(box.total==0){
                        wrapper.code=LiveDataWrapper.CODE_OK
                        wrapper.value=0
                        liveData.postValue(wrapper)
                    }
                    else if (box.songList==null||box.songList.size == 0&&res.searchSongSheetWrapper2.total!=0) {
                        wrapper.code = LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    } else {
                        callback.invoke(box.songList)
                        if(total!=res.searchSongSheetWrapper2.total){
                            wrapper.code=LiveDataWrapper.CODE_OK
                            wrapper.value=res.searchSongSheetWrapper2.total
                            liveData.postValue(wrapper)
                            total=res.searchSongSheetWrapper2.total
                        }
                    }
                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleSongSheet>) {

    }
}