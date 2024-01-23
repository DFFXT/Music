package com.web.moudle.music.player

import android.content.ContentUris
import android.content.Context
import android.media.audiofx.Equalizer
import android.os.Binder
import android.provider.MediaStore
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.Player
import com.web.common.base.PlayerObserver
import com.web.common.constant.AppConfig
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.data.Music
import com.web.moudle.music.player.core.IExoPlayer
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.MusicDataSource
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plug.PlayTypePlug
import com.web.moudle.net.proxy.InternetProxy
import com.music.m.R
import java.io.FileNotFoundException
import java.io.IOException

class ExoPlayerConnection(
    private val ctx: Context,
    private val player: IExoPlayer,
    private val playInterfaceManager: PlayInterfaceManager,
    private val dataSource: MusicDataSource,
    private val equalizer: Equalizer
) : Binder(), IMusicControl {
    private val playTypePlug: PlayTypePlug = PlayTypePlug(this, player, dataSource)
    val config = PlayerConfig

    init {
        player.getExoPlayer().addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_ENDED -> {
                        next(true)
                    }
                }
            }
        })
    }

    override fun getMediaSessionId(): Int = player.getAudioSessionId()
    override fun getEqualizer(): Equalizer = equalizer
    override fun changePlayerPlayingStatus() {
        if (player.isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    override fun changePlayType(playType: PlayerConfig.PlayType) {
        config.playType = playType
        playInterfaceManager.onPlayTypeChanged(playType)
    }

    override fun play(music: Music) {
        loadMusic(music, true)
    }

    override fun addObserver(lifecycleOwner: LifecycleOwner?, playerObserver: PlayerObserver) {
        playInterfaceManager.addObserver(lifecycleOwner, playerObserver)
    }

    override fun removeObserver(lifecycleOwner: LifecycleOwner?, playerObserver: PlayerObserver?) {
        playInterfaceManager.removeObserver(lifecycleOwner, playerObserver)
    }

    override fun getPlayerInfo(lifecycleOwner: LifecycleOwner?) {
        disPatchMusicInfo(lifecycleOwner)
    }

    override fun pause() {
        if (player.isPlaying()) {
            player.pause()
        }
        playInterfaceManager.onPause()
    }

    override fun play() {
        if (!player.isPlaying()) {
            player.start()
        }
        playInterfaceManager.onPlay()
    }

    override fun play(index: Int, origin: PlayerConfig.MusicOrigin) {
        if (origin == PlayerConfig.MusicOrigin.WAIT) {
            dataSource.setIndex(index)
        } else {
            dataSource.addMusic(dataSource.localList[index], origin)
        }
        dataSource.getCurrentMusic()?.let {
            loadMusic(it, true)
        }
    }

    override fun addWait(index: Int) {
        dataSource.addMusic(dataSource.localList[index], PlayerConfig.MusicOrigin.WAIT)
        if (dataSource.size == 1) {
            loadMusic(music = dataSource[0], autoPlay = true)
        }
    }

    override fun next(auto: Boolean) {
        playTypePlug.next(auto)
    }

    override fun pre() {
        playTypePlug.pre()
    }

    override fun seekTo(millis: Int) {
        player.seekTo(millis.toLong())
        playInterfaceManager.onCurrentTime(millis, getCurrentMusic()?.duration ?: 0)
    }

    override fun getDataSource(): MusicDataSource = dataSource

    override fun getCurrentMusic(): Music? = dataSource.getCurrentMusic()

    override fun getCurrentTime(): Int {
        return player.getCurrentDuration().toInt()
    }

    override fun getMusic(index: Int): Music = dataSource[index]

    override fun remove(index: Int) {
        if (index < 0 || index > dataSource.size) return
        if (index == 0 && dataSource.size == 1) { // ***移除最后一个
            dataSource.remove(index)
            next(false)
        } else if (index != dataSource.index) { // **移除播放之前的
            dataSource.remove(index)
        } else if (index == dataSource.index) { // **移除正在播放的
            dataSource.remove(index)
            next(false)
        }
    }

    override fun loadMusic(music: Music, autoPlay: Boolean) {
        config.isHasInit = true
        config.setMusic(music)
        player.reset()
        try {
            config.isPrepared = false
            if (music.localUriId == 0L) {
                player.load(InternetProxy.proxyUrl(music.path), autoPlay)
            } else {
                player.load(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, music.localUriId), autoPlay)
            }
            // 触发状态变化，默认状态立即变化，即使如果播放网络音乐会缓冲一段时间
            if (autoPlay) {
                playInterfaceManager.onPlay()
            }
            // player.setDataSource(InternetProxy.proxyUrl(music.path), autoPlay)
            // player.prepareAsync()
            AppConfig.lastMusic = music.path
            playInterfaceManager.onLoad(music, music.duration)
        } catch (e: IOException) { // ***播放异常，如果是文件不存在则删除记录
            if (e is FileNotFoundException) {
                MToast.showToast(ctx, ResUtil.getString(R.string.fileNotFound))
            }
            if (config.musicOrigin == PlayerConfig.MusicOrigin.STORAGE) {
                MToast.showToast(ctx, ResUtil.getString(R.string.cannotPlay))
            } else {
                next(true)
            }
        }
        // playInterfaceManager.onMusicOriginChanged(config.musicOrigin)
    }

    private fun disPatchMusicInfo(owner: LifecycleOwner?) {
        playInterfaceManager.onMusicListChange(owner, dataSource.localList)
        playInterfaceManager.onLoad(getCurrentMusic(), getCurrentMusic()?.duration ?: 0)
        if (!player.isPlaying()) {
            playInterfaceManager.pause(owner)
        }
        playInterfaceManager.onCurrentTime(player.getCurrentDuration().toInt(), player.getDuration().toInt())
        playInterfaceManager.musicOriginChanged(owner, config.musicOrigin)
        playInterfaceManager.playTypeChanged(owner, config.playType)
    }
}