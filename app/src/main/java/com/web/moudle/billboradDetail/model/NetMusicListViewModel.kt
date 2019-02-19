package com.web.moudle.billboradDetail.model

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.moudle.billboradDetail.adapter.NetMusicListDataSource
import com.web.moudle.billboradDetail.bean.BillBoardInfo
import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.net.retrofit.ResultTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.web.R
import java.text.SimpleDateFormat
import java.util.*

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