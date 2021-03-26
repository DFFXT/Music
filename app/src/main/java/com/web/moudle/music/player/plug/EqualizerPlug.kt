package com.web.moudle.music.player.plug

import android.media.audiofx.Equalizer
import com.web.moudle.lyrics.EqualizerActivity
import com.web.moudle.music.player.CorePlayer
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

/**
 * 均衡器plug
 */
class EqualizerPlug(player: CorePlayer):ServiceLifeCycle {
    val equalizer by lazy { Equalizer(0, player.audioSessionId) }
    override fun onCreate() {
        //**设置均衡器
        equalizer.enabled = true
        EqualizerActivity.saveDefaultSoundEffect(equalizer)
        val soundInfos = EqualizerActivity.getCurrentSoundEffect()
        for (i in soundInfos.indices) {
            equalizer.setBandLevel(i.toShort(), (soundInfos[i].value + soundInfos[i].min).toShort())
        }
    }

    override fun onDestroy() {

    }
}