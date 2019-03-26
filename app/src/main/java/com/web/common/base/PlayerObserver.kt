package com.web.common.base

import com.web.data.Music
import com.web.data.MusicList
import com.web.data.PlayerConfig
import com.web.moudle.music.player.PlayInterface

open class PlayerObserver:PlayInterface {
    override fun play() {

    }

    override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {

    }

    override fun pause() {

    }

    override fun currentTime(group: Int, child: Int, time: Int) {

    }

    override fun musicListChange(group: Int,child: Int,list: MutableList<MusicList<Music>>?) {

    }

    override fun playTypeChanged(playType: PlayerConfig.PlayType?) {

    }

    override fun musicOriginChanged(origin: PlayerConfig.MusicOrigin?) {

    }

    override fun bufferingUpdate(percent: Int) {

    }
}