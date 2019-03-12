package com.web.web

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.web.common.base.BaseActivity
import com.web.common.util.PermissionManager
import com.web.data.Music
import com.web.data.MusicList
import com.web.moudle.music.page.local.MusicActivity
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicEntry.ui.PlayerObserver

/**
 * logo界面
 * 权限请求
 * 初始化播放器，获取播放列表
 *
 */
class HelloActivity : BaseActivity() {
    private val code = 0x999
    override fun getLayoutId(): Int = R.layout.activity_hello
    private var connect: MusicPlay.Connect? = null
    var connection: ServiceConnection? = null


    private val observer = object : PlayerObserver() {
        override fun musicListChange(group: Int, list: MutableList<MusicList<Music>>?) {
            connect?.removeObserver(this)
            MusicActivity.actionStartForResult(this@HelloActivity, code)
        }
    }

    override fun initView() {
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                connect = (service as MusicPlay.Connect)
                connect?.addObserver(this@HelloActivity, observer)
                if (PermissionManager.requestIOPermission(this@HelloActivity)) {
                    connect?.getList(0)
                }
            }
        }
        val intent = Intent(this, MusicPlay::class.java)
        intent.action = MusicPlay.BIND
        bindService(intent, connection!!, Context.BIND_AUTO_CREATE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == code) {
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }
        connect?.getList(0)
    }

    override fun onDestroy() {
        unbindService(connection!!)
        super.onDestroy()
    }
}