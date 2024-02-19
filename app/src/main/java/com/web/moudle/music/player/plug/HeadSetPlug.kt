package com.web.moudle.music.player.plug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.web.app.MyApplication
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

//**耳塞插拔注册广播
class HeadSetPlug(control: IMusicControl) : ServiceLifeCycle {
    private val headsetReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //if (!config.isHasInit()) return
            if (intent.getIntExtra("state", 0) == 0) {
                control.pause()
            } else {
                // 不能插上耳机就进行播放
                // control.play()
            }
        }
    }

    override fun onCreate() {
        val filter = IntentFilter()
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG)
        MyApplication.context.registerReceiver(headsetReceiver, filter)
    }

    override fun onDestroy() {
        MyApplication.context.unregisterReceiver(headsetReceiver)
    }
}