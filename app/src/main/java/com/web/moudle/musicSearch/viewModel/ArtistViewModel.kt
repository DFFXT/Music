package com.web.moudle.musicSearch.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo
import com.web.moudle.musicSearch.model.ArtistDataSource
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistViewModel : ViewModel() {

    val artistId = MutableLiveData<String>()
    private val model = BaseRetrofit()
    val status = MutableLiveData<LiveDataWrapper<Int>>()
    private val dataSource = ArtistDataSource(status)
    private var config: PagedList.Config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20)
            .setEnablePlaceholders(false)
            .build()

    fun search(keyword: String?): LiveData<PagedList<SimpleArtistInfo>> {
        dataSource.keyword = keyword
        dataSource.page = 1
        return LivePagedListBuilder(object : DataSource.Factory<String, SimpleArtistInfo>() {
            override fun create(): DataSource<String, SimpleArtistInfo> {
                return dataSource
            }
        }, config).build()
    }

    /**
     * 用假id经过请求获取重定向的地址，地址里面包含真正的id
     */
    fun getRedirectHeader(fakeId: String) {
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

    }

}