package com.web.moudle.music.player.core

import android.net.Uri

interface IPlayer {
    fun seekTo(microSecond: Long)
    fun pause()
    fun start()
    fun load(source: String, autoPlay: Boolean)
    fun load(uri: Uri, autoPlay: Boolean)
    fun getDuration(): Long
    fun getCurrentDuration(): Long
    fun getAudioSessionId(): Int
    fun isPlaying(): Boolean
    fun reset()
}