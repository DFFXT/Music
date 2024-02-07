package com.web.moudle.music.player.plug

import android.app.Service
import com.web.data.Music
import com.web.moudle.music.player.other.PlayInterface
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle
import com.web.moudle.notification.MusicNotification

class NotificationPlug(service: Service) : PlayInterface,ServiceLifeCycle {
    private val config = PlayerConfig
    private val notification by lazy { MusicNotification(service) }

    override fun onCreate() {

    }
    override fun onPlay() {
        notification.setPlayStatus(true)
        notifyChange()
    }

    override fun onLoad(music: Music?, maxTime: Int) {
        if (music == null) {
            notification.cancel()
        } else {
            notification.setName(music.musicName)
            notification.setSinger(music.singer)
            notification.setBitMap(config.bitmap)
            notifyChange()
        }
    }

    override fun onPause() {
        notification.setPlayStatus(false)
        notifyChange()
    }
    private fun notifyChange() {
        if (config.music == null) {
            notification.cancel()
        } else {
            notification.notifyChange()
        }
    }

    override fun onDestroy() {

    }
}