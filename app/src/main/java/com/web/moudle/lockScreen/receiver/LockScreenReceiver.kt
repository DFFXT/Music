package com.web.moudle.lockScreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.web.moudle.lockScreen.ui.LockScreenActivity

class LockScreenReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent==null||intent.action==null||intent.action!=Intent.ACTION_SCREEN_OFF) return
        val intent1=Intent(context,LockScreenActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent1)

    }
}