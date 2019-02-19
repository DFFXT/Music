package com.web.common.base

import android.app.*
import android.content.Context
import android.os.Build
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.widget.RemoteViews
import com.web.web.R

/**
 * 原生通知
 */
abstract class BaseNotification(private val context: Context,id: String, name: String) {
    private var builder: Notification.Builder
    private var notificationId=0

    init {
        createChannel(id, name)
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, id)
        } else {
            Notification.Builder(context)
        }
        builder.setSmallIcon(R.drawable.ic_launcher)
    }

    protected fun createChannel(id: String, name: String) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null,null)
            manager().createNotificationChannel(channel)
        }
    }
    protected fun manager():NotificationManager{
        return context.getSystemService(NotificationManager::class.java)
    }


    @CallSuper
    open fun notifyChange() {
        update(builder)
        builder.setAutoCancel(false)
        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR

        manager().notify(notificationId,notification)
    }

    fun cancel() {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancel(notificationId)
    }

    abstract fun update(builder: Notification.Builder)


}