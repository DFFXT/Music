package com.web.moudle.albumEntry.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.musicEntry.bean.MusicDetailInfo

class AlbumEntryViewModel : ViewModel() {
    private val model = AlbumEntryModel()
    val albumResponse = MutableLiveData<LiveDataWrapper<AlbumResponse>>()
    val detailMusic = MutableLiveData<LiveDataWrapper<MusicDetailInfo>>()
    val lyrics = MutableLiveData<LiveDataWrapper<ArrayList<LyricsLine>>>()
    private val albumWrapper=LiveDataWrapper<AlbumResponse>()
    private val detailMusicWrapper=LiveDataWrapper<MusicDetailInfo>()

    fun getAlbumInfo(albumId: String) {
        model.getAlbumInfo(albumId)
                .get(
                        onNext = {res->
                            albumWrapper.code= LiveDataWrapper.CODE_OK
                            albumWrapper.value=res
                            albumResponse.value=albumWrapper
                        },
                        onError = {
                            it.printStackTrace()
                            albumWrapper.code= LiveDataWrapper.CODE_ERROR
                            albumResponse.value=albumWrapper
                        }
                )
    }
    fun getDetail(songId: String) {
        model.getMusicDetail(songId)
                .get(
                        onNext = {res->
                            detailMusicWrapper.code= LiveDataWrapper.CODE_OK
                            detailMusicWrapper.value=res
                            detailMusic.value=detailMusicWrapper
                        },
                        onError = {
                            detailMusicWrapper.code= LiveDataWrapper.CODE_ERROR
                            detailMusic.value=detailMusicWrapper
                        }
                )
    }

}