package com.web.moudle.entry.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.web.common.base.BaseSingleObserver
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.config.Shortcut
import com.web.moudle.entry.bean.MusicDetailInfo
import com.web.moudle.music.page.lyrics.model.LyricsLine

class DetailMusicViewModel : ViewModel() {
    companion object {
        const val CODE_INTERNET_ERROR=1
        const val CODE_NO_DATA=2
        const val CODE_ERROR=2
        const val CODE_OK=2
    }
    private val model = MusicDetailModel()
    val detailMusic = MutableLiveData<LiveDataWrapper<MusicDetailInfo>>()
    val lyrics = MutableLiveData<LiveDataWrapper<ArrayList<LyricsLine>>>()
    private val lyricsWrapper = LiveDataWrapper<ArrayList<LyricsLine>>()
    private val detailMusicWrapper=LiveDataWrapper<MusicDetailInfo>()

    fun getDetail(songId: String) {
        model.getMusicDetail(songId)
                .get(
                        onNext = {res->
                            detailMusicWrapper.code= CODE_OK
                            detailMusicWrapper.value=res
                            detailMusic.value=detailMusicWrapper
                        },
                        onError = {
                            detailMusicWrapper.code= CODE_ERROR
                            detailMusic.value=detailMusicWrapper
                        }
                )
    }
    fun getLyrics(lrcLink:String){
        if(Shortcut.isStrictEmpty(lrcLink)){
            lyricsWrapper.code= CODE_NO_DATA
            lyrics.value=lyricsWrapper
            return
        }
        model.getLyrics(lrcLink)
                .subscribe(object :BaseSingleObserver<ArrayList<LyricsLine>>(){
                    override fun error(e: Throwable) {
                        lyricsWrapper.code= CODE_ERROR
                        lyrics.value=lyricsWrapper
                    }

                    override fun onSuccess(t: ArrayList<LyricsLine>) {
                        lyricsWrapper.code= CODE_OK
                        lyricsWrapper.value=t
                        lyrics.value=lyricsWrapper
                    }
                })
    }
}