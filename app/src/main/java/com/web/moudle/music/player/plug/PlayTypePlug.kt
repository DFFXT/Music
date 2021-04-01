package com.web.moudle.music.player.plug

import androidx.lifecycle.LifecycleOwner
import com.web.common.base.PlayerObserver
import com.web.data.IgnoreMusic
import com.web.data.Music
import com.web.moudle.music.player.*
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.MusicDataSource
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.other.PlayerConfig.PlayType

/**
 * 只实现了部分复杂的播放控制
 */
class PlayTypePlug(private val control: IMusicControl,
                   private val player: CorePlayer,
                   private val dataSource: MusicDataSource): IMusicControl {
    val randomSystem = RandomSystem()
    private val config = player.config

    override fun addWait(index: Int) {
        TODO("Not yet implemented")
    }
    override fun play(index: Int, origin: PlayerConfig.MusicOrigin) {
        TODO("Not yet implemented")
    }

    override fun getDataSource(): MusicDataSource {
        TODO("Not yet implemented")
    }

    override fun changePlayerPlayingStatus() {
        TODO("Not yet implemented")
    }

    override fun play(music: Music) {
        TODO("Not yet implemented")
    }

    override fun addObserver(lifecycleOwner: LifecycleOwner?, playerObserver: PlayerObserver) {
        TODO("Not yet implemented")
    }

    override fun remove(index: Int) {
        TODO("Not yet implemented")
    }

    override fun seekTo(millis: Int) {
        TODO("Not yet implemented")
    }

    override fun changePlayType(playType: PlayType) {
        TODO("Not yet implemented")
    }

    override fun removeObserver(lifecycleOwner: LifecycleOwner?, playerObserver: PlayerObserver?) {
        TODO("Not yet implemented")
    }

    override fun getPlayerInfo(lifecycleOwner: LifecycleOwner?) {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun next(auto: Boolean) {
        if (auto && config.playType != PlayType.RANDOM){
            control.play(dataSource.nextIndex(), config.musicOrigin)
            return
        }
        if (!auto){
            config.musicOrigin = PlayerConfig.MusicOrigin.LOCAL
            control.play(dataSource.nextIndex(), config.musicOrigin)
            return
        }
        when (config.playType) {
            PlayType.ALL_LOOP -> {
                control.play(dataSource.nextIndex(), config.musicOrigin)
            }
            PlayType.ONE_LOOP -> {
                player.seekTo(0)
                control.play()
            }
            PlayType.ALL_ONCE -> {
                if (dataSource.index < dataSource.size - 1) {
                    control.play(dataSource.nextIndex(), config.musicOrigin)
                } else { //***暂停
                    control.pause()
                }
            }
            PlayType.ONE_ONCE -> {
                control.pause()
            }
            PlayType.RANDOM -> {
                val index = randomSystem.getRandomNumber().toInt()
                control.play(index,  config.musicOrigin)
            }
        }
    }



    override fun pre() {
        dataSource.changeToLocal()
        control.play(dataSource.preIndex(), PlayerConfig.MusicOrigin.LOCAL)
    }

    override fun getCurrentMusic(): Music {
        TODO("Not yet implemented")
    }

    override fun getCurrentTime(): Int {
        TODO("Not yet implemented")
    }

    override fun getMusic(index: Int): Music {
        TODO("Not yet implemented")
    }



    fun addIntRangeWithFilter(from: Int, length: Int, list: List<Music>) {
        for (i in from until length + from) {
            if (!IgnoreMusic.isIgnoreMusic(list[i])) {
                randomSystem.addNumber(i)
            }
        }
    }
}