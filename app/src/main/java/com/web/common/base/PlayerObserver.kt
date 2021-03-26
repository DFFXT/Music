package com.web.common.base

import com.web.data.Music
import com.web.data.MusicList
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.other.PlayInterface

open class PlayerObserver: PlayInterface {
    override fun onPlay() {

    }

    override fun onPlayTypeChanged(playType: PlayerConfig.PlayType?) {

    }

    override fun onMusicOriginChanged(origin: PlayerConfig.MusicOrigin?) {

    }

    override fun onBufferingUpdate(percent: Int) {

    }
}