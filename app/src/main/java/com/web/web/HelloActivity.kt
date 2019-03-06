package com.web.web

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.web.common.base.BaseActivity
import com.web.common.util.WindowUtil
import com.web.data.Music
import com.web.data.MusicList
import com.web.moudle.music.page.local.MusicActivity
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicEntry.ui.PlayerObserver

class HelloActivity:BaseActivity() {
    override fun getLayoutId(): Int =R.layout.activity_hello
    private var connect:MusicPlay.Connect?=null
    var connection:ServiceConnection?=null

    private val observer=object :PlayerObserver(){
        override fun musicListChange(group: Int, list: MutableList<MusicList<Music>>?) {
            connect?.removeObserver(this)
            MusicActivity.actionStart(this@HelloActivity)
        }
    }

    override fun initView() {

        connection=object :ServiceConnection{
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                connect=(service as MusicPlay.Connect)
                connect?.addObserver(this@HelloActivity,observer)
                connect?.getList(0)
            }
        }
        val intent= Intent(this,MusicPlay::class.java)
        intent.action = MusicPlay.BIND
        bindService(intent,connection!!, Context.BIND_AUTO_CREATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        finish()
    }

    override fun onDestroy() {
        unbindService(connection!!)
        super.onDestroy()
    }
}