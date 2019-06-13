package com.web.moudle.musicDownload.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import androidx.annotation.CallSuper
import com.web.common.base.BaseFragment
import com.web.data.InternetMusic
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.web.moudle.service.FileDownloadService

abstract class BaseDownloadFragment:BaseFragment() , FileDownloadService.DownloadListener{
    var connect: FileDownloadService.Connect?=null
    private var serviceConnection=object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            connect = service as FileDownloadService.Connect
            connect?.addDownloadListener(this@BaseDownloadFragment)
            connect?.getDownloadList()
        }
        override fun onServiceDisconnected(name: ComponentName) {}
    }

    @CallSuper
    override fun initView(rootView: View) {
        val intent = Intent(context, FileDownloadService::class.java)
        context?.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    override fun progressChange(id: Int, progress: Long, max: Long) {
    }

    override fun complete(music: InternetMusic?) {
    }

    override fun statusChange(id: Int, isDownload: Boolean) {
    }

    override fun listChanged(downloadMusicList: MutableList<DownloadMusic>, completeList: MutableList<DownloadMusic>) {
    }
    override fun onDestroy() {
        super.onDestroy()
        connect?.removeDownloadListener(this)
    }
}