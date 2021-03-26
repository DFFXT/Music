package com.web.moudle.music.player.plug

import android.content.Intent
import com.web.moudle.music.player.plugInterface.IntentReceiver
import com.web.moudle.music.player.plugInterface.Plug
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

class PlugDispatcher : IntentReceiver, ServiceLifeCycle, MutableList<Plug> by ArrayList() {
    override fun dispatch(intent: Intent) {
        forEach {
            if (it is IntentReceiver) {
                it.dispatch(intent)
            }
        }
    }

    override fun onCreate() {
        forEach {
            if (it is ServiceLifeCycle) {
                it.onCreate()
            }
        }
    }

    override fun onDestroy() {
        forEach {
            if (it is ServiceLifeCycle) {
                it.onDestroy()
            }
        }
    }
}