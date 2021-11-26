package com.web.moudle.music.player

import android.media.MediaPlayer
import android.net.Uri
import com.google.android.exoplayer2.BasePlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.*
import com.web.app.MyApplication
import com.web.moudle.music.player.core.IExoPlayer
import com.web.moudle.music.player.other.PlayerConfig

@Deprecated("ExoPlayer instead")
class CorePlayer : MediaPlayer() {
    var autoPlay = false
        private set
    val config = PlayerConfig

    fun setDataSource(path: String?, autoPlay: Boolean) {
        super.setDataSource(path)
        this.autoPlay = autoPlay
    }
}

class ExoPlayer : IExoPlayer {
    private val mPlayer = SimpleExoPlayer.Builder(MyApplication.context)
        /*.setMediaSourceFactory(
            DefaultMediaSourceFactory(DefaultDataSourceFactory(MyApplication.context, object :DataSource.Factory))
        )*/
        .build()

    override fun getExoPlayer(): BasePlayer = mPlayer

    override fun seekTo(microSecond: Long) {
        mPlayer.seekTo(microSecond)

    }

    override fun pause() {
        mPlayer.pause()
    }

    override fun start() {
        mPlayer.play()
    }

    override fun load(source: String, autoPlay: Boolean) {
        mPlayer.setPlaybackSpeed(1f)
        mPlayer.playWhenReady = autoPlay
        mPlayer.setMediaItem(MediaItem.fromUri(source))
        mPlayer.prepare()
    }

    override fun load(uri: Uri, autoPlay: Boolean) {
        mPlayer.playWhenReady = autoPlay
        mPlayer.setMediaItem(MediaItem.fromUri(uri))
        mPlayer.prepare()
    }

    override fun isPlaying(): Boolean = mPlayer.isPlaying

    override fun reset() {}

    override fun getCurrentDuration(): Long = mPlayer.currentPosition
    override fun getDuration(): Long = mPlayer.duration

    override fun getAudioSessionId(): Int = mPlayer.audioSessionId
}