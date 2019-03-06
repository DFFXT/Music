package com.web.moudle.music.player

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.web.data.Music
import com.web.data.MusicList
import com.web.data.PlayerConfig
import java.util.*

class PlayInterfaceManager:PlayInterface {
    private var observerList=LinkedList<PlayInterface>()
    fun removeObserver(observer: PlayInterface){
        observerList.remove(observer)
    }
    fun addObserver(owner:LifecycleOwner,observer:PlayInterface){
        if(!this.observerList.contains(observer)){
            this.observerList.add(observer)
        }
        //**观测者变动

        owner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(event==Lifecycle.Event.ON_DESTROY){
                    observerList.remove(observer)
                    owner.lifecycle.removeObserver(this)
                }
            }
        })
    }
    override fun play() {
        observerList.forEach {
            it.play()
        }
    }

    override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
        observerList.forEach {
            it.load(groupIndex,childIndex,music,maxTime)
        }
    }

    override fun pause() {
        observerList.forEach {
            it.pause()
        }
    }

    override fun currentTime(group: Int, child: Int, time: Int) {
        observerList.forEach {
            it.currentTime(group, child, time)
        }
    }

    override fun musicListChange(group: Int, list: MutableList<MusicList<Music>>?) {
        observerList.forEach {
            it.musicListChange(group, list)
        }
    }

    override fun playTypeChanged(playType: PlayerConfig.PlayType?) {
        observerList.forEach {
            it.playTypeChanged(playType)
        }
    }

    override fun musicOriginChanged(origin: PlayerConfig.MusicOrigin?) {
        observerList.forEach {
            it.musicOriginChanged(origin)
        }
    }

    override fun bufferingUpdate(percent: Int) {
        observerList.forEach {
            it.bufferingUpdate(percent)
        }
    }
}