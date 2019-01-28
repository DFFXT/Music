package com.web.moudle.musicSearch.model

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.web.common.base.BaseObserver
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.next.SearchSongSheetWrapper1
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet

class SheetDataSource(private val liveData: MutableLiveData<LiveDataWrapper<Throwable>>) : PageKeyedDataSource<String, SimpleSongSheet>() {
    var keyword: String? = null
    var page: Int = 1
    private val wrapper = LiveDataWrapper<Throwable>()
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
                    val t = res.searchSongSheetWrapper2.songList
                    if (t.size == 0) {
                        wrapper.code = LiveDataWrapper.CODE_NO_DATA
                        liveData.postValue(wrapper)
                    } else {
                        callback.invoke(t)
                    }
                }
            })
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleSongSheet>) {

    }
}