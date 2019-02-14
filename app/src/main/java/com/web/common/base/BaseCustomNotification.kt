package com.web.common.base

import android.app.*
import android.content.Context
import android.os.Build
import android.support.annotation.LayoutRes
import android.widget.RemoteViews
import com.web.web.R


abstract class BaseCustomNotification(private val context: Context, private val id: String, name: String, @LayoutRes layout: Int):BaseNotification(
        context,id,name
) {
    private var view: RemoteViews? = null
    private var builder: Notification.Builder

    init {
        createChannel(id, name)
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, id)
        } else {
            Notification.Builder(context)
        }
        view = RemoteViews(context.packageName, layout)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(view)
        } else {
            builder.setContent(view)
        }
        builder.setSmallIcon(R.drawable.defaultfile)
    }





    override fun update(builder: Notification.Builder) {
        update(view!!)
    }


    abstract fun update(view: RemoteViews)


}