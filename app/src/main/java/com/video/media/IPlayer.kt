package com.video.media

interface IPlayer {
    fun load(path: String)
    fun play()
    fun pause()
    fun release()
    fun seekTo(mills: Int)
    fun prepare()
}