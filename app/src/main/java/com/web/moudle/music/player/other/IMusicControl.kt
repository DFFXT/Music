package com.web.moudle.music.player.other

import androidx.lifecycle.LifecycleOwner
import com.web.common.base.PlayerObserver
import com.web.data.Music

interface IMusicControl {
    fun changePlayerPlayingStatus()
    fun pause()
    fun play()
    fun play(index: Int,origin: PlayerConfig.MusicOrigin)
    fun play(music: Music)
    fun next(auto:Boolean)
    fun pre()
    fun getCurrentMusic():Music?
    fun getDataSource(): MusicDataSource
    fun getCurrentTime():Int
    fun getMusic(index:Int):Music
    fun addObserver(lifecycleOwner: LifecycleOwner?,playerObserver: PlayerObserver)
    fun removeObserver(lifecycleOwner: LifecycleOwner?, playerObserver: PlayerObserver?)
    fun getPlayerInfo(lifecycleOwner: LifecycleOwner?)
    fun remove(index: Int)
    fun seekTo(millis:Int)
    fun changePlayType(playType: PlayerConfig.PlayType)
    //fun delete(deleteFile:Boolean, vararg index: Int)
    //fun play(music: Music)
}