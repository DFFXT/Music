package com.web.moudle.musicDownload.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.web.common.base.BaseNotification

class DownloadNotification(ctx: Context) : BaseNotification(ctx, "downloaing", "d") {

    private var pIntent: PendingIntent
    private var count=0

    init {
        val intent = Intent(ctx, MusicDownLoadActivity::class.java)
        pIntent = PendingIntent.getActivity(ctx, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun notifyChange(count:Int) {
        this.count=count
        notifyChange()
    }
    override fun update(builder: Notification.Builder) {
        builder.setContentTitle("正在下载")
        builder.setContentText("${count}首歌曲正在下载")
        builder.setContentIntent(pIntent)
    }
}