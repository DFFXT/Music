package com.web.moudle.musicSearch.viewModel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet
import com.web.moudle.musicSearch.model.SheetDataSource

class SheetViewModel : ViewModel() {

    val artistId = MutableLiveData<String>()
    //private val model = BaseRetrofit()
    val status = MutableLiveData<LiveDataWrapper<Throwable>>()
    private val dataSource = SheetDataSource(status)
    private var config: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20)
            .setEnablePlaceholders(false)
            .build()

    fun search(keyword: String?): LiveData<PagedList<SimpleSongSheet>> {
        dataSource.keyword = keyword
        dataSource.page = 1
        return LivePagedListBuilder(object : DataSource.Factory<String, SimpleSongSheet>() {
            override fun create(): DataSource<String, SimpleSongSheet> {
                return dataSource
            }
        }, config).build()
    }

    /**
     * 用假id经过请求获取重定向的地址，地址里面包含真正的id
     */
    /*fun getRedirectHeader(fakeId: String) {
        model.obtainClassNoConverter(NetApis.SingerEntry::class.java)
                .getArtistId(fakeId)
                .enqueue(object : Callback<Any> {
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        artistId.value = null
                    }

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        var flg = false
                        response.raw().request().url()?.let { url ->
                            val it = url.toString()
                            val lastIndex = it.lastIndexOf("/")
                            artistId.value = it.substring(lastIndex + 1)
                            flg = true
                        }
                        if (!flg) {
                            artistId.value = null
                        }
                    }

                })

    }*/

}