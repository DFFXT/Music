package com.web.moudle.music.player

import android.media.MediaPlayer
import android.net.Uri
import com.google.android.exoplayer2.*
import com.web.app.MyApplication
import com.web.moudle.music.player.core.IExoPlayer
import com.web.moudle.music.player.core.IPlayer
import com.web.moudle.music.player.other.PlayInterfaceManager
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
    var autoPlay = false
    private val mPlayer = SimpleExoPlayer.Builder(MyApplication.context)
        .build()

    override fun getExoPlayer(): BasePlayer = mPlayer

    override fun seekTo(microSecond: Long) {
        mPlayer.seekTo(microSecond)
        // mPlayer.setSeekParameters(SeekParameters(microSecond, microSecond))
    }

    override fun pause() {
        mPlayer.pause()
    }

    override fun start() {
        mPlayer.play()
    }

    override fun load(source: String, autoPlay: Boolean) {
        this.autoPlay = autoPlay
        mPlayer.setMediaItem(MediaItem.fromUri(source))
        mPlayer.prepare()
    }

    override fun load(uri: Uri, autoPlay: Boolean) {
        this.autoPlay = autoPlay
        mPlayer.setMediaItem(MediaItem.fromUri(uri))
    }

    override fun isPlaying(): Boolean = mPlayer.isPlaying

    override fun reset() {}

    override fun getCurrentDuration(): Long = mPlayer.currentPosition
    override fun getDuration(): Long = mPlayer.duration

    override fun getAudioSessionId(): Int = mPlayer.audioSessionId
}