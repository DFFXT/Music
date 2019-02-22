package com.web.moudle.musicSearch.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.model.AlbumDataSource

class AlbumViewModel:ViewModel() {

    val status=MutableLiveData<LiveDataWrapper<Int>>()
    private val dataSource= AlbumDataSource(status)
    private var config:PagedList.Config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20)
            .setEnablePlaceholders(false)
            .build()

    fun search(keyword:String?):LiveData<PagedList<SimpleAlbumInfo>>{
        dataSource.keyword=keyword
        dataSource.page=1
        return LivePagedListBuilder(object :DataSource.Factory<String, SimpleAlbumInfo>(){
            override fun create(): DataSource<String, SimpleAlbumInfo> {
                return dataSource
            }
        },config).build()
    }


}