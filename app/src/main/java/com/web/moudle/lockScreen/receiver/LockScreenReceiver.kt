package com.web.moudle.lockScreen.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.web.common.util.IOUtil
import com.web.moudle.lockScreen.ui.LockScreenActivity
import java.util.*

class LockScreenReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context==null||intent==null||intent.action==null||intent.action!=Intent.ACTION_SCREEN_OFF) return
        IOUtil.log(Date().toString()+"\n"+intent.toString())
        LockScreenActivity.actionStart(context)
    }
}