package com.web.moudle.music.player

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LifecycleOwner
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
    private val plugDispatcher = PlugDispatcher()
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
        //插件加载
        val floatWindowPlug = FloatWindowPlug(control)
        val playTypePlug = PlayTypePlug(control, player, musicDataSource)
        plugDispatcher.add(LockScreenPlug(this))
        plugDispatcher.add(HeadSetPlug(control))
        plugDispatcher.add(PhoneStatePlug(control))
        plugDispatcher.add(ActionControlPlug(control, player, playTypePlug, musicDispatcher, musicDataSource))
        plugDispatcher.add(floatWindowPlug)
        plugDispatcher.add(equalizerPlug)
        musicDispatcher.addObserver(null, floatWindowPlug)
        musicDispatcher.addObserver(null, NotificationPlug(this, player.config))
        musicDispatcher.addObserver(null, musicDataSource)
        TickerPlug(musicDispatcher, player).let {
            plugDispatcher.add(it)
            musicDispatcher.addObserver(null, it)
        }
        plugDispatcher.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null){
            plugDispatcher.dispatch(intent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(arg0: Intent): IBinder {
        return control
    }

    override fun onDestroy() {
        super.onDestroy()
        plugDispatcher.onDestroy()
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