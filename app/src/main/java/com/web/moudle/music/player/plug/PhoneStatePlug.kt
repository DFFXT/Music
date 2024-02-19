package com.web.moudle.music.player.plug

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.web.app.MyApplication
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

/**
 * 来电监听plug
 */
class PhoneStatePlug(control: IMusicControl) : ServiceLifeCycle {
    private val phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String) {
            //if (!config.isHasInit()) return
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    control.pause()
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    // 不能电话一关闭就立即播放
                    // control.play()
                }
            }
        }
    }

    override fun onCreate() {
        MyApplication.context.getSystemService(TelephonyManager::class.java)?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onDestroy() {
        MyApplication.context.getSystemService(TelephonyManager::class.java)?.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
}