package com.web.moudle.musicEntry.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.BaseSingleObserver
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.config.Shortcut
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.musicEntry.bean.MusicDetailInfo

class DetailMusicViewModel : ViewModel() {
    private val model = MusicDetailModel()
    val detailMusic = MutableLiveData<LiveDataWrapper<MusicDetailInfo>>()
    val lyrics = MutableLiveData<LiveDataWrapper<ArrayList<LyricsLine>>>()
    private val lyricsWrapper = LiveDataWrapper<ArrayList<LyricsLine>>()
    private val detailMusicWrapper=LiveDataWrapper<MusicDetailInfo>()

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
    fun getLyrics(lrcLink:String){
        if(Shortcut.isStrictEmpty(lrcLink)){
            lyricsWrapper.code= LiveDataWrapper.CODE_NO_DATA
            lyricsWrapper.value=ArrayList()
            lyrics.value=lyricsWrapper
            return
        }
        model.getLyrics(lrcLink)
                .subscribe(object :BaseSingleObserver<ArrayList<LyricsLine>>(){
                    override fun error(e: Throwable) {
                        lyricsWrapper.code= LiveDataWrapper.CODE_ERROR
                        lyrics.value=lyricsWrapper
                    }

                    override fun onSuccess(t: ArrayList<LyricsLine>) {
                        lyricsWrapper.code= LiveDataWrapper.CODE_OK
                        lyricsWrapper.value=t
                        lyrics.value=lyricsWrapper
                    }
                })
    }
}