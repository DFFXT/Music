package com.web.moudle.music.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.MusicDataSource
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.music.player.plug.AudioFocusPlug
import com.web.moudle.music.player.plug.EqualizerPlug
import com.web.moudle.music.player.plug.FloatWindowPlug
import com.web.moudle.music.player.plug.HeadSetPlug
import com.web.moudle.music.player.plug.LockScreenPlug
import com.web.moudle.music.player.plug.MediaSessionServerPlug
import com.web.moudle.music.player.plug.NotificationPlug
import com.web.moudle.music.player.plug.PhoneStatePlug
import com.web.moudle.music.player.plug.TickerPlug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class NewPlayer : Service() {
    private val musicDispatcher = PlayInterfaceManager()
    private val player = ExoPlayer()
    private val musicDataSource = MusicDataSource()
    private val equalizerPlug = EqualizerPlug(player)
    private val control: IMusicControl by lazy {
        GlobalScope.launch(Dispatchers.IO) {
            delay(50)
            ActionControlPlug.scan(this@NewPlayer)
        }
        //PlayerConnection(this, player, musicDispatcher, musicDataSource, equalizerPlug.equalizer)
        ExoPlayerConnection(this, player, musicDispatcher, musicDataSource, equalizerPlug.equalizer)
    }

    override fun onCreate() {

        /*startForeground(99, createNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING)*/

        super.onCreate()
        musicDispatcher.add(null, LockScreenPlug(this))
        musicDispatcher.add(null, HeadSetPlug(control))
        musicDispatcher.add(null, MediaSessionServerPlug(control))
        musicDispatcher.add(null, MediaSessionServerPlug(control))
        musicDispatcher.add(null, PhoneStatePlug(control))
        musicDispatcher.add(null, ActionControlPlug(control, player, musicDispatcher, musicDataSource))
        musicDispatcher.add(null, FloatWindowPlug(control))
        musicDispatcher.add(null, equalizerPlug)
        musicDispatcher.add(null, NotificationPlug(this))
        musicDispatcher.add(null, musicDataSource)
        musicDispatcher.add(null, TickerPlug(musicDispatcher, player))
        musicDispatcher.add(null, AudioFocusPlug(control))
        musicDispatcher.onCreate()
    }

    private fun createNotification(): Notification {
        // Create your notification channel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        }
        val channel = NotificationChannel(
            "channel_id",
            "Channel Name",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService<NotificationManager?>(NotificationManager::class.java)
            .createNotificationChannel(channel)

        // Build notification
        return Notification.Builder(this, channel.id)
            .setContentTitle("Service Running")
            .setContentText("Working in background")
            .setSmallIcon(com.music.m.R.drawable.ic_launcher)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            musicDispatcher.dispatch(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(arg0: Intent): IBinder {
        return control as IBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        musicDispatcher.onDestroy()
    }

    companion object {
        @JvmStatic
        fun bind(ctx: Context, connection: ServiceConnection) {
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.action = ActionControlPlug.BIND
            // 在API 35上需要先启动前台服务然后才能绑定服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(intent)
            }
            ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}