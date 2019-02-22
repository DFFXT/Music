package com.web.moudle.billboradDetail.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.billboradDetail.adapter.NetMusicListDataSource
import com.web.moudle.billboradDetail.bean.BillBoardInfo
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo

class NetMusicListViewModel : ViewModel() {

    //private val model = NetMusicListModel()
    //val netMusicList = MutableLiveData<NetMusicBox>()
    val response=MutableLiveData<LiveDataWrapper<BillBoardInfo>>()

    private fun makeLiveData(dataSource: DataSource<String,SimpleMusicInfo>):LiveData<PagedList<SimpleMusicInfo>>{
        return LivePagedListBuilder(object :DataSource.Factory<String,SimpleMusicInfo>(){
            override fun create(): DataSource<String, SimpleMusicInfo> {
                return dataSource
            }
        },PagedList.Config.Builder()
                .setPageSize(30)
                .setInitialLoadSizeHint(20)
                .setEnablePlaceholders(true)
                .build()).build()
    }

    fun requestList(type:Int):LiveData<PagedList<SimpleMusicInfo>>{
        val dataSource=NetMusicListDataSource(response,type)
        return makeLiveData(dataSource)
    }

    fun requestRecommend():LiveData<PagedList<SimpleMusicInfo>>{
        val dataSource=NetMusicListDataSource(response)
        return makeLiveData(dataSource)
    }
    fun requestSingerAllMusic(uid:String):LiveData<PagedList<SimpleMusicInfo>>{
        val dataSource=NetMusicListDataSource(response,uid,NetMusicListDataSource.TYPE_MUSIC)
        return makeLiveData(dataSource)
    }

    fun requestSingerAllAlbum(uid:String):LiveData<PagedList<SimpleMusicInfo>>{
        val dataSource=NetMusicListDataSource(response,uid,NetMusicListDataSource.TYPE_ALBUM)
        return makeLiveData(dataSource)
    }
}