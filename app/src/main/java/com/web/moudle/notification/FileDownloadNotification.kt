package com.web.moudle.notification

import android.content.Context
import android.widget.RemoteViews
import com.web.common.base.BaseCustomNotification
import com.web.common.util.ResUtil
import com.music.m.R

class FileDownloadNotification(ctx:Context):BaseCustomNotification(ctx,2,"downloadFile",ResUtil.getString(R.string.setting_update), R.layout.layout_download_progress) {

    private var percent=0f
    private var title=""
    fun notify(title:String,percent:Float){
        this.percent=percent
        this.title=title
        notifyChange()
    }
    override fun update(view: RemoteViews) {
        view.setTextViewText(R.id.tv_title,title)
        view.setTextViewText(R.id.tv_percent,"${percent.toInt()}%")
        view.setProgressBar(R.id.progressBar,100,percent.toInt(),false)
    }
}