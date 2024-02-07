package com.web.moudle.music.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import com.music.m.R
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.MusicDataSource
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plug.*
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
            ctx.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}