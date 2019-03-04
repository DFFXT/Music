package com.web.moudle.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.web.common.base.BaseNotification
import com.web.moudle.musicDownload.ui.MusicDownLoadActivity

class DownloadNotification(ctx: Context) : BaseNotification(ctx, "downloading", "d") {

    private var pIntent: PendingIntent
    private var count=0

    init {
        val intent = Intent(ctx, MusicDownLoadActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
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