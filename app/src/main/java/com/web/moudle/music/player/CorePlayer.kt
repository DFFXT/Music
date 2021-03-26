package com.web.moudle.music.player

import android.media.MediaPlayer
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.other.PlayerConfig

class CorePlayer(playInterfaceManager: PlayInterfaceManager) : MediaPlayer() {
    var autoPlay = false
        private set
    val config = PlayerConfig(playInterfaceManager)

    fun setDataSource(path: String?, autoPlay:Boolean) {
        super.setDataSource(path)
        this.autoPlay = autoPlay
    }
}