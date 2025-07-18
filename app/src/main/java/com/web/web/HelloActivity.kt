package com.web.web

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import com.compose.MainActivity
import com.music.m.R
import com.music.m.databinding.ActivityHelloBinding
import com.web.common.base.BaseActivity
import com.web.common.base.BaseViewBindingActivity
import com.web.common.base.PlayerObserver
import com.web.common.util.PermissionManager
import com.web.data.Music
import com.web.moudle.home.HomePageActivity
import com.web.moudle.music.page.local.MusicActivity
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.other.IMusicControl

/**
 * logo界面
 * 权限请求
 * 初始化播放器，获取播放列表
 *
 */
class HelloActivity : BaseViewBindingActivity<ActivityHelloBinding>() {
    private val code = 0x999
    private var connect: IMusicControl? = null
    private lateinit var connection: ServiceConnection

    private val observer = object : PlayerObserver() {
        override fun onMusicListChange(list: MutableList<Music>?) {
            connect?.removeObserver(this@HelloActivity, null)
            // MusicActivity.actionStartForResult(this@HelloActivity, code)
           // MusicActivity.actionStart(this@HelloActivity)
            MainActivity.actionStart(this@HelloActivity)
        }
    }

    override fun initView() {
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                connect = (service as IMusicControl)
                connect?.addObserver(this@HelloActivity, observer)
            }
        }

        if (PermissionManager.requestAllPermission(this@HelloActivity)) {
            NewPlayer.bind(this, connection)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == code) {
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }
        NewPlayer.bind(this, connection)
    }

    override fun onDestroy() {
        unbindService(connection)
        super.onDestroy()
    }
}