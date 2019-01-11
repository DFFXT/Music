package com.web.moudle.singerEntry.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.singerEntry.bean.AlbumEntryBox
import com.web.moudle.singerEntry.bean.AlbumEntryItem
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.bean.SongEntryBox

class SingerEntryViewModel:ViewModel() {

    val singerInfo=MutableLiveData<LiveDataWrapper<SingerInfo>>()
    private val singerInfoWrapper=LiveDataWrapper<SingerInfo>()

    val songList=MutableLiveData<LiveDataWrapper<SongEntryBox>>()
    private val songListWrapper=LiveDataWrapper<SongEntryBox>()

    val albumList=MutableLiveData<LiveDataWrapper<AlbumEntryBox>>()
    private val albumListWrapper=LiveDataWrapper<AlbumEntryBox>()


    private val model=SingerEntryModel()

    fun getArtistInfo(uid:String){
        model.getArtistInfo(uid)
                .get(onNext = {
                    singerInfoWrapper.code= LiveDataWrapper.CODE_OK
                    singerInfoWrapper.value=it
                    singerInfo.value=singerInfoWrapper
                },onError = {
                    singerInfoWrapper.code=LiveDataWrapper.CODE_ERROR
                    singerInfo.value=singerInfoWrapper
                })
    }

    fun getSongList(uid:String,offset:Int,limit:Int){
        model.getSongList(uid,offset,limit)
                .get(onNext = {
                    songListWrapper.code=LiveDataWrapper.CODE_OK
                    songListWrapper.value=it
                    songList.value=songListWrapper
                },onError = {
                    it.printStackTrace()
                })
    }

    fun getAlbumList(uid:String,offset:Int,limit:Int){
        model.getAlbumList(uid,offset,limit)
                .get(onNext = {
                    albumListWrapper.code=LiveDataWrapper.CODE_OK
                    albumListWrapper.value=it
                    albumList.value=albumListWrapper
                },onError = {
                    it.printStackTrace()
                })
    }

}