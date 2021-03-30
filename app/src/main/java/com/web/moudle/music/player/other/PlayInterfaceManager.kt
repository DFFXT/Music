package com.web.moudle.music.player.other

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.web.data.Music
import com.web.moudle.music.player.plugInterface.IntentReceiver
import com.web.moudle.music.player.plugInterface.Plug
import com.web.moudle.music.player.plugInterface.ServiceLifeCycle

class PlayInterfaceManager : IntentReceiver, PlayInterface, ServiceLifeCycle {
    private val observerList = HashMap<LifecycleOwner?, ArrayList<PlayInterface>>()
    private val dispatchers = ArrayList<Plug>()
    fun removeObserver(owner: LifecycleOwner?) {
        observerList.remove(owner)
    }

    fun add(owner: LifecycleOwner?, observer: Any) {
        if (observer is PlayInterface) {
            addObserver(owner, observer)
        }
        if (observer is Plug) {
            dispatchers.add(observer)
        }
    }

    fun addObserver(owner: LifecycleOwner?, observer: PlayInterface) {
        this.observerList[owner] = this.observerList[owner] ?: ArrayList()
        if (!this.observerList[owner]!!.contains(observer)) {
            observerList[owner]!!.add(observer)
        }


        //**观测者变动,在activity中可以不进行手动释放，如果没有传入lifecycle就需要手动释放
        owner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    observerList.remove(source)
                    owner.lifecycle.removeObserver(this)
                }
            }
        })
    }

    fun play(owner: LifecycleOwner?) {
        observerList[owner]?.forEach {
            it.onPlay()
        }
    }

    override fun onPlay() {
        observerList.forEach {
            it.value.forEach { it.onPlay() }
        }
    }

    fun load(owner: LifecycleOwner?, groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
        //observerList[owner]?.onLoad(groupIndex,childIndex,music,maxTime)
    }

    override fun onLoad(music: Music?, maxTime: Int) {
        observerList.forEach {
            it.value.forEach { it.onLoad(music, maxTime) }
        }
    }

    fun pause(owner: LifecycleOwner?) {
        observerList[owner]?.forEach { it.onPause() }
    }

    override fun onPause() {
        observerList.forEach {
            it.value.forEach { it.onPause() }
        }
    }

    fun currentTime(owner: LifecycleOwner?, group: Int, child: Int, time: Int) {
        //observerList[owner]?.onCurrentTime(group, child, time)
    }

    override fun onCurrentTime(duration: Int, maxTime: Int) {
        observerList.forEach {
            it.value.forEach { it.onCurrentTime(duration, maxTime) }
        }
    }

    fun onMusicListChange(owner: LifecycleOwner?, list: List<Music>?) {
        observerList[owner]?.forEach { it.onMusicListChange(list) }
    }

    override fun onMusicListChange(list: MutableList<Music>?) {
        observerList.forEach {
            it.value.forEach { it.onMusicListChange(list) }
        }
    }

    fun playTypeChanged(owner: LifecycleOwner?, playType: PlayerConfig.PlayType?) {
        observerList[owner]?.forEach { it.onPlayTypeChanged(playType) }
    }

    override fun onPlayTypeChanged(playType: PlayerConfig.PlayType?) {
        observerList.forEach {
            it.value.forEach { it.onPlayTypeChanged(playType) }
        }
    }

    fun musicOriginChanged(owner: LifecycleOwner?, origin: PlayerConfig.MusicOrigin?) {
        observerList[owner]?.forEach { it.onMusicOriginChanged(origin) }
    }

    override fun onMusicOriginChanged(origin: PlayerConfig.MusicOrigin?) {
        observerList.forEach {
            it.value.forEach { it.onMusicOriginChanged(origin) }
        }
    }

    fun bufferingUpdate(owner: LifecycleOwner?, percent: Int) {
        observerList[owner]?.forEach { it.onBufferingUpdate(percent) }
    }

    override fun onBufferingUpdate(percent: Int) {
        observerList.forEach {
            it.value.forEach { it.onBufferingUpdate(percent) }
        }
    }

    override fun dispatch(intent: Intent) {
        dispatchers.forEach {
            if (it is IntentReceiver) {
                it.dispatch(intent)
            }
        }
    }

    override fun onCreate() {
        dispatchers.forEach {
            if (it is ServiceLifeCycle) {
                it.onCreate()
            }
        }
    }

    override fun onDestroy() {
        dispatchers.forEach {
            if (it is ServiceLifeCycle) {
                it.onDestroy()
            }
        }
    }
}