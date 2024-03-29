package com.web.moudle.music.player.plug

import com.web.common.tool.Ticker
import com.web.data.Music
import com.web.moudle.music.player.core.IPlayer
import com.web.moudle.music.player.other.PlayInterface
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle
import kotlinx.coroutines.Dispatchers.Main

class TickerPlug(private val playerInterface: PlayInterface, player: IPlayer) : PlayInterface, ServiceLifeCycle {
    private val ticker = Ticker(500, 0, Main) {
        playerInterface.onCurrentTime(player.getCurrentDuration().toInt(), player.getDuration().toInt())
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
        ticker.stop()
    }

    override fun onPlay() {
        ticker.start()
    }

    override fun onPause() {
        ticker.stop()
    }

    override fun onLoad(music: Music?, maxTime: Int) {
        music?.let {
            ticker.start()
        }
    }
}