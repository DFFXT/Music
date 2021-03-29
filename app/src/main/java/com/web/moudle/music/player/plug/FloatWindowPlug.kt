package com.web.moudle.music.player.plug

import android.content.Intent
import com.web.app.MyApplication
import com.web.data.Music
import com.web.moudle.music.player.other.FloatLyricsManager
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayInterface
import com.web.moudle.music.player.plugInterface.IntentReceiver
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle
import com.web.moudle.setting.lyrics.LyricsSettingActivity.Companion.lyricsOverlap

/**
 * 歌词浮窗plug
 */
class FloatWindowPlug(private val control: IMusicControl):IntentReceiver ,ServiceLifeCycle, PlayInterface{
    private var manager : FloatLyricsManager? = null

    override fun onCreate() {
        lyricsFloatWindowChange()
    }

    override fun onDestroy() {
    }

    override fun dispatch(intent: Intent) {
        if (intent.action == ActionControlPlug.ACTION_FLOAT_WINDOW_CHANGE){
            lyricsFloatWindowChange()
        }
    }

    override fun onLoad(music: Music?, maxTime: Int) {
        lyricsFloatWindowChange()
    }
    private fun lyricsFloatWindowChange() {
        if (lyricsOverlap()) {
            if (manager == null) {
                manager = FloatLyricsManager(MyApplication.context, control)
            }
            manager?.refresh()
        } else {
            manager?.close()
        }
    }
}