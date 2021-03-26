package com.web.moudle.music.player.plugInterface

import android.content.Intent

interface IntentReceiver :Plug{
    abstract fun dispatch(intent: Intent)
}