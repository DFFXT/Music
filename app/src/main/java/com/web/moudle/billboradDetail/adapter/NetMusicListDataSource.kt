package com.web.moudle.billboradDetail.adapter


import androidx.annotation.IntDef
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.moudle.billboradDetail.NetMusicType
import com.web.moudle.billboradDetail.bean.BillBoardInfo
import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.billboradDetail.model.NetMusicListModel
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.net.retrofit.ResultTransform
import com.web.web.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NetMusicListDataSource() : PageKeyedDataSource<String, SimpleMusicInfo>() {
    private lateinit var wrapper: MutableLiveData<LiveDataWrapper<BillBoardInfo>>
    private val w=LiveDataWrapper<BillBoardInfo>()
    private lateinit var urlType: NetMusicType
    private var billboardType: Int=-1
    private lateinit var uid:String
    companion object {
        const val TYPE_MUSIC=1
        const val TYPE_ALBUM=2
        @IntDef(TYPE_ALBUM, TYPE_MUSIC)
        @Retention(AnnotationRetention.SOURCE)
        annotation class TYPE
    }


    constructor(wrapper:MutableLiveData<LiveDataWrapper<BillBoardInfo>>,billboardType:Int) : this() {
        this.wrapper=wrapper
        this.urlType=NetMusicType.TYPE_BILLBOARD
        this.billboardType=billboardType
    }
    constructor(wrapper:MutableLiveData<LiveDataWrapper<BillBoardInfo>>):this(){
        this.wrapper=wrapper
        this.urlType=NetMusicType.TYPE_TODAY_RECOMMEND
    }
    constructor(wrapper:MutableLiveData<LiveDataWrapper<BillBoardInfo>>,uid:String,@TYPE type:Int):this(){
        this.wrapper=wrapper
        if(type == TYPE_MUSIC){
            this.urlType=NetMusicType.TYPE_SINGER_ALL_MUSIC
        }else if(type== TYPE_ALBUM){
            this.urlType=NetMusicType.TYPE_SINGER_ALL_ALBUM
        }
        this.uid=uid
    }

    private val model = NetMusicListModel()
    private var page = 1
    private var pageSize = 30
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SimpleMusicInfo>) {
        load {
            callback.onResult(it.list,"","")
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SimpleMusicInfo>) {
        load {
            callback.onResult(it.list,"")
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SimpleMusicInfo>) {

    }

    private fun load(callback:((NetMusicBox)->Unit)) {
        when (urlType) {
            NetMusicType.TYPE_BILLBOARD -> {
                model.requestList(billboardType)
                        .get(
                                onNext = {
                                    page++
                                    w.value=it.billboardInfo
                                    w.code=LiveDataWrapper.CODE_OK
                                    wrapper.postValue(w)

                                    w.value=it.billboardInfo
                                    w.code=LiveDataWrapper.CODE_OK
                                    wrapper.postValue(w)

                                    callback.invoke(it)
                                },
                                onError = {
                                    wrapper.postValue(null)
                                }
                        )
            }
            NetMusicType.TYPE_SINGER_ALL_MUSIC -> {
                model.requestSingerAllMusic(uid,(page-1)*pageSize,pageSize)
                        .get(
                                onNext = {
                                    if(it.songList?.size?:0==0){
                                        return@get
                                    }
                                    else if(it.haveMore!=1){
                                        w.code=LiveDataWrapper.CODE_NO_DATA
                                    }
                                    page++
                                    val b = BillBoardInfo()
                                    b.billboard_songnum=it.total.toString()
                                    b.name=it.songList!![0].author
                                    b.color=ResUtil.getString(R.string.netMusicTextColor)
                                    b.bg_color=ResUtil.getString(R.string.netMusicBgColor)

                                    w.value=b
                                    w.code=LiveDataWrapper.CODE_OK
                                    wrapper.postValue(w)

                                    callback.invoke(NetMusicBox(it.songList,b))
                                },
                                onError = {
                                    w.code=LiveDataWrapper.CODE_ERROR
                                    wrapper.postValue(w)
                                }
                        )
            }
            NetMusicType.TYPE_SINGER_ALL_ALBUM->{
                model.requestSingerAllAlbum(uid,(page-1)*pageSize,pageSize)
                        .get(
                                onNext = {
                                    if(it.albumList?.size?:0==0){
                                        return@get
                                    }
                                    else if(it.haveMore!=1){
                                        w.code=LiveDataWrapper.CODE_NO_DATA
                                    }
                                    page++
                                    val b = BillBoardInfo()
                                    b.billboard_songnum=it.num.toString()
                                    b.name=it.albumList!![0].artistName
                                    b.color=ResUtil.getString(R.string.netMusicTextColor)
                                    b.bg_color=ResUtil.getString(R.string.netMusicBgColor)

                                    w.value=b
                                    w.code=LiveDataWrapper.CODE_OK
                                    wrapper.postValue(w)



                                    callback.invoke(NetMusicBox(mapper(it.albumList),b))
                                },
                                onError = {
                                    w.code=LiveDataWrapper.CODE_ERROR
                                    wrapper.postValue(w)
                                }
                        )
            }
            NetMusicType.TYPE_TODAY_RECOMMEND -> {
                model.requestRecommend(page,pageSize)
                        .compose(ResultTransform())
                        .get(
                                onNext = {

                                    page++
                                    val b = BillBoardInfo()
                                    b.update_date= SimpleDateFormat("YYYY MM dd", Locale.CHINA).format(Date(it.date))
                                    b.billboard_songnum=it.total.toString()
                                    b.name= ResUtil.getString(R.string.todayRecommend)
                                    b.color=it.color
                                    b.bg_color=it.bg_color
                                    w.value=b
                                    w.code=LiveDataWrapper.CODE_OK
                                    wrapper.postValue(w)

                                    val box = NetMusicBox(it.list, b)
                                    callback.invoke(box)
                                },
                                onError = {
                                    w.code=LiveDataWrapper.CODE_ERROR
                                    wrapper.postValue(w)
                                }
                        )
            }
        }
    }

    /**
     * 启动对象兼容
     */
    private fun mapper(albumList:List<SimpleAlbumInfo>):List<SimpleMusicInfo>{
        val list=ArrayList<SimpleMusicInfo>()
        albumList.forEach {
            list.add(SimpleMusicInfo(
                    albumId = it.albumId,
                    songId = it.albumId,
                    albumImage = it.albumImage,
                    albumTitle =it.albumName,
                    allArtistId = it.artistId,
                    allRate = "96",
                    allUid = uid,
                    author = it.artistName,
                    hasFilmTv = "",
                    hasMV = 0,
                    musicName = it.albumName,
                    picSmall = it.albumImage,
                    siProxyCompany = "",
                    quality = "",
                    uid = uid
            ))
        }
        return list
    }
}