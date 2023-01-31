package com.web.moudle.music.player.plug

import android.media.audiofx.Equalizer
import com.web.moudle.lyrics.EqualizerActivity
import com.web.moudle.music.player.core.IPlayer
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

/**
 * 均衡器plug
 */
class EqualizerPlug(player: IPlayer) : ServiceLifeCycle {
    val equalizer by lazy { Equalizer(0, player.getAudioSessionId()) }
    override fun onCreate() {
        // **设置均衡器
        equalizer.enabled = true
        EqualizerActivity.saveDefaultSoundEffect(equalizer)
        val soundInfos = EqualizerActivity.getCurrentSoundEffect()
        for (i in soundInfos.indices) {
            equalizer.setBandLevel(i.toShort(), (soundInfos[i].value + soundInfos[i].min).toShort())
        }
    }

    override fun onDestroy() {
        equalizer.release()
    }
}