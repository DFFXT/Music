package com.web.moudle.music.player.plug

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.web.common.constant.AppConfig
import com.web.moudle.lockScreen.receiver.LockScreenReceiver
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.plugInterface.IntentReceiver
import com.web.moudle.music.player.plugInterface.Plug
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

/**
 * 锁屏plug
 */
class LockScreenPlug(private val service: Service) : Plug, ServiceLifeCycle ,IntentReceiver{
    private var lockScreenReceiver: LockScreenReceiver? = null

    override fun onCreate() {
        lockScreen(!AppConfig.noLockScreen)
    }

    override fun onDestroy() {
        lockScreen(false)
    }

    override fun dispatch(intent: Intent) {
        if (intent.action == ACTION_LOCKSCREEN){
            lockScreen(!AppConfig.noLockScreen)
        }
    }

    fun lockScreen(lock: Boolean) {
        if (lock && lockScreenReceiver == null) {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            service.registerReceiver(LockScreenReceiver().also { lockScreenReceiver = it }, filter)
        } else if (!lock && lockScreenReceiver != null) {
            service.unregisterReceiver(lockScreenReceiver)
            lockScreenReceiver = null
        }
    }
    companion object{
        const val ACTION_LOCKSCREEN = "ACTION_LOCKSCREEN"
        @JvmStatic
        fun lockScreen(ctx:Context){
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.action = ACTION_LOCKSCREEN
            ctx.startService(intent)
        }
    }
}