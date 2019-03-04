package com.web.common.base

import android.app.Notification
import android.content.Context
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import com.web.web.R


abstract class BaseCustomNotification(context: Context,id: String, name: String, @LayoutRes layout: Int):BaseNotification(
        context,id,name
) {
    private var view: RemoteViews? = null

    init {
        view = RemoteViews(context.packageName, layout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(view)
        } else {
            builder.setContent(view)
        }
    }





    override fun update(builder: Notification.Builder) {
        update(view!!)
    }


    abstract fun update(view: RemoteViews)


}