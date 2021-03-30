package com.web.moudle.music.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.web.moudle.music.player.other.MusicDataSource
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.plug.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewPlayer : Service() {
    private val musicDispatcher = PlayInterfaceManager()
    private val player = CorePlayer(musicDispatcher)
    private val musicDataSource = MusicDataSource(player.config)
    private val equalizerPlug = EqualizerPlug(player)
    private val control: PlayerConnection by lazy {
        GlobalScope.launch(Dispatchers.IO) {
            delay(50)
            ActionControlPlug.scan(this@NewPlayer)
        }
        PlayerConnection(this, player, musicDispatcher, musicDataSource, equalizerPlug.equalizer)
    }

    override fun onCreate() {
        super.onCreate()
        musicDispatcher.add(null, LockScreenPlug(this))
        musicDispatcher.add(null, HeadSetPlug(control))
        musicDispatcher.add(null, PhoneStatePlug(control))
        musicDispatcher.add(null, ActionControlPlug(control, player, musicDispatcher, musicDataSource))
        musicDispatcher.add(null, FloatWindowPlug(control))
        musicDispatcher.add(null, equalizerPlug)
        musicDispatcher.add(null, NotificationPlug(this, player.config))
        musicDispatcher.add(null, musicDataSource)
        musicDispatcher.add(null, TickerPlug(musicDispatcher, player))
        musicDispatcher.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            musicDispatcher.dispatch(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(arg0: Intent): IBinder {
        return control
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