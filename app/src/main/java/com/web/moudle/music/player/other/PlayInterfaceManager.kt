package com.web.moudle.music.player.other

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.web.data.Music
import com.web.data.MusicList
import kotlin.collections.HashMap

class PlayInterfaceManager: PlayInterface {
    private val observerList=HashMap<LifecycleOwner?,PlayInterface>()
    fun removeObserver(owner: LifecycleOwner?){
        observerList.remove(owner)
    }
    fun addObserver(owner:LifecycleOwner?,observer: PlayInterface){
        if(!this.observerList.containsKey(owner)){
            observerList[owner] = observer
        }


        //**观测者变动,在activity中可以不进行手动释放，如果没有传入lifecycle就需要手动释放
        owner?.lifecycle?.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if(event==Lifecycle.Event.ON_DESTROY){
                    observerList.remove(source)
                    owner.lifecycle.removeObserver(this)
                }
            }
        })
    }
    fun play(owner: LifecycleOwner?){
        observerList[owner]?.play()
    }
    override fun play() {
        observerList.forEach {
            it.value.play()
        }
    }

    fun load(owner: LifecycleOwner?,groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
        observerList[owner]?.load(groupIndex,childIndex,music,maxTime)
    }
    override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
        observerList.forEach {
            it.value.load(groupIndex,childIndex,music,maxTime)
        }
    }
    fun pause(owner: LifecycleOwner?){
        observerList[owner]?.pause()
    }
    override fun pause() {
        observerList.forEach {
            it.value.pause()
        }
    }
    fun currentTime(owner: LifecycleOwner?,group: Int, child: Int, time: Int){
        observerList[owner]?.currentTime(group, child, time)
    }
    override fun currentTime(group: Int, child: Int, time: Int) {
        observerList.forEach {
            it.value.currentTime(group, child, time)
        }
    }
    fun musicListChange(owner: LifecycleOwner?,group: Int,child: Int, list: MutableList<MusicList<Music>>?){
        observerList[owner]?.musicListChange(group,child, list)
    }

    override fun musicListChange(group: Int,child: Int, list: MutableList<MusicList<Music>>?) {
        observerList.forEach {
            it.value.musicListChange(group,child, list)
        }
    }

    fun playTypeChanged(owner: LifecycleOwner?,playType: PlayerConfig.PlayType?){
        observerList[owner]?.playTypeChanged(playType)
    }
    override fun playTypeChanged(playType: PlayerConfig.PlayType?) {
        observerList.forEach {
            it.value.playTypeChanged(playType)
        }
    }

    fun musicOriginChanged(owner: LifecycleOwner?,origin: PlayerConfig.MusicOrigin?) {
        observerList[owner]?.musicOriginChanged(origin)
    }
    override fun musicOriginChanged(origin: PlayerConfig.MusicOrigin?) {
        observerList.forEach {
            it.value.musicOriginChanged(origin)
        }
    }
    fun bufferingUpdate(owner: LifecycleOwner?,percent: Int) {
        observerList[owner]?.bufferingUpdate(percent)
    }
    override fun bufferingUpdate(percent: Int) {
        observerList.forEach {
            it.value.bufferingUpdate(percent)
        }
    }
}