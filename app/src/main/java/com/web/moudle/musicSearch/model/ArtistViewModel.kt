package com.web.moudle.musicSearch.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.SimpleArtistInfo

class ArtistViewModel:ViewModel() {

    val status=MutableLiveData<LiveDataWrapper<Throwable>>()
    private val dataSource=SingerDataSource(status)
    private var config:PagedList.Config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20)
            .setEnablePlaceholders(false)
            .build()

    fun search(keyword:String?):LiveData<PagedList<SimpleArtistInfo>>{
        dataSource.keyword=keyword
        dataSource.page=1
        return LivePagedListBuilder(object :DataSource.Factory<String,SimpleArtistInfo>(){
            override fun create(): DataSource<String, SimpleArtistInfo> {
                return dataSource
            }
        },config).build()
    }


}