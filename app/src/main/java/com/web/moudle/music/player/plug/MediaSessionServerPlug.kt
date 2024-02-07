package com.web.moudle.music.player.plug

import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.MediaSession.Callback
import android.media.session.PlaybackState
import android.os.SystemClock
import com.web.app.MyApplication
import com.web.data.Music
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayInterface
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

/**
 * 对接MediaSession服务，比如车机蓝牙音乐，使app支持受控
 */
class MediaSessionServerPlug(private val control: IMusicControl) : ServiceLifeCycle, PlayInterface {
    private lateinit var session: MediaSession
    override fun onCreate() {
        session = MediaSession(MyApplication.context, "MediaSession")
        session.isActive = true
        session.setCallback(object : Callback() {
            override fun onPause() {
                control.pause()
            }

            override fun onSkipToNext() {
                control.next(true)
            }

            override fun onSkipToPrevious() {
                control.pre()
            }

            override fun onPlay() {
                control.play()
            }

            override fun onSeekTo(pos: Long) {
                control.seekTo(pos.toInt())
            }
        })
    }

    override fun onPlay() {
        session.setPlaybackState(
            PlaybackState
                .Builder()
                // 设置当前状态支持的Action
                .setActions(
                    PlaybackState.ACTION_PAUSE or
                        PlaybackState.ACTION_SKIP_TO_NEXT or
                        PlaybackState.ACTION_SKIP_TO_PREVIOUS or PlaybackState.ACTION_SEEK_TO,
                )
                .setState(PlaybackState.STATE_PLAYING, 0, 1f, SystemClock.elapsedRealtime())
                .build(),
        )
    }

    override fun onCurrentTime(duration: Int, maxTime: Int) {
        session.setPlaybackState(
            PlaybackState
                .Builder()
                .setActions(
                    PlaybackState.ACTION_PAUSE or
                        PlaybackState.ACTION_SKIP_TO_NEXT or
                        PlaybackState.ACTION_SKIP_TO_PREVIOUS or PlaybackState.ACTION_SEEK_TO,
                )
                .setState(PlaybackState.STATE_PLAYING, duration.toLong(), 1f, SystemClock.elapsedRealtime())
                .build(),
        )
    }

    override fun onPause() {
        session.setPlaybackState(
            PlaybackState
                .Builder()
                .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_SKIP_TO_NEXT or PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_PAUSED, control.getCurrentTime().toLong(), 1f, SystemClock.elapsedRealtime())
                .build(),
        )
    }

    override fun onLoad(music: Music?, maxTime: Int) {
        session.setMetadata(
            MediaMetadata
                .Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, music?.musicName ?: "")
                .putLong(MediaMetadata.METADATA_KEY_DURATION, maxTime.toLong())
                .putString(MediaMetadata.METADATA_KEY_ARTIST, music?.singer ?: "")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, music?.album ?: "")
                .putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, PlayerConfig.bitmap)
                .build(),
        )
    }

    override fun onDestroy() {
        session.release()
    }
}
