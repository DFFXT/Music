package com.web.common.base

import android.app.Notification
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.LayoutRes


abstract class BaseCustomNotification(context: Context,
                                      notificationId:Int,
                                      id: String,
                                      name: String,
                                      @LayoutRes layout: Int):BaseNotification(
        context,notificationId,id,name
) {
    val view: RemoteViews = RemoteViews(context.packageName, layout)

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(view)
        } else {
            builder.setContent(view)
        }
    }





    override fun update(builder: Notification.Builder) {
        update(view)
    }


    abstract fun update(view: RemoteViews)


}