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
import com.web.common.base.PlayerObserver
import com.web.moudle.home.HomePageActivity

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
        override fun musicListChange(group: Int,child:Int,list: MutableList<MusicList<Music>>?) {
            connect?.removeObserver(this@HelloActivity)
           // MusicActivity.actionStartForResult(this@HelloActivity, code)
            HomePageActivity.actionStart(this@HelloActivity)
        }
    }

    override fun initView() {
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder) {
                connect = (service as MusicPlay.Connect)
                connect?.addObserver(this@HelloActivity, observer)
                connect?.getList(0)
            }
        }



        if(PermissionManager.requestAllPermission(this@HelloActivity)){
            bind()
        }
    }
    private fun bind(){
        val intent = Intent(this, MusicPlay::class.java)
        intent.action = MusicPlay.BIND
        bindService(intent, connection!!, Context.BIND_AUTO_CREATE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)
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
        bind()
    }

    override fun onDestroy() {
        unbindService(connection!!)
        super.onDestroy()
    }
}