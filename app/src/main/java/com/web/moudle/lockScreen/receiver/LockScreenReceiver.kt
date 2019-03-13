package com.web.moudle.lockScreen.receiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.web.moudle.lockScreen.ui.LockScreenActivity

class LockScreenReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null||intent==null||intent.action==null||intent.action!=Intent.ACTION_SCREEN_OFF) return
        LockScreenActivity.actionStart(context)
    }
}