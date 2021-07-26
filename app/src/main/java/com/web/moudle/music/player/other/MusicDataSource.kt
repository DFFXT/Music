package com.web.moudle.music.player.other

import com.web.common.base.ChineseComparator
import com.web.data.IgnoreMusic
import com.web.data.Music
import com.web.moudle.music.player.findIndexFirst
import com.web.moudle.music.player.findIndexLast
import kotlin.collections.ArrayList

class MusicDataSource : MutableList<Music> by ArrayList(), PlayInterface {
    private val config = PlayerConfig
    var localList = ArrayList<Music>() // 原始数据
    var index = -1
        private set
    val localIndex
        get() = localList.indexOf(this.getOrNull(index))
    private fun setStorageMusic(music: Music) {
        clear()
        add(music)
        index = 0
    }
    private fun setInternetMusic(music: Music) {
        // todo 由于接口问题目前不支持在线音乐了
    }
    fun addMusic(music: Music, origin: PlayerConfig.MusicOrigin) {
        if (config.musicOrigin != origin) {
            config.musicOrigin = origin
        }
        when (origin) {
            PlayerConfig.MusicOrigin.WAIT -> {
                add(music)
            }
            PlayerConfig.MusicOrigin.STORAGE -> {
                setStorageMusic(music)
            }
            PlayerConfig.MusicOrigin.LOCAL -> {
                // 无操作
                changeToLocal()
                index = localList.indexOf(music)
            }
            PlayerConfig.MusicOrigin.INTERNET -> {
                setInternetMusic(music)
            }
        }
    }

    fun setIndex(index: Int) {
        this.index = index
    }

    fun getCurrentMusic(): Music? = getOrNull(index)

    fun sort() {
        localList.sortWith { m1: Music, m2: Music -> ChineseComparator.compare(m1.musicName, m2.musicName) }
    }

    fun remove(index: Int) {
        if (index < 0 || index > this.size) return
        if (index < this.size) { // **移除播放之前的
            this.index--
            this.removeAt(index)
        } else if (index > this.index && index < this.size) { // **移除之后播放的
            this.removeAt(index)
        } else if (index == this.index) { // **移除正在播放的
            this.index--
            this.removeAt(index)
        }
    }

    /**
     * 获取上一曲
     * @return 没有音乐则返回-1 有则返回下标
     */
    fun preIndex(): Int {
        if (this.isEmpty()) return -1
        val index: Int = if (this.index - 1 < 0) {
            (this.size - 1).coerceAtLeast(0)
        } else {
            this.index - 1
        }
        var tempIndex = index
        if (config.musicOrigin == PlayerConfig.MusicOrigin.LOCAL) {
            tempIndex = this.findIndexLast(0, index) { m: Music, _ -> !IgnoreMusic.isIgnoreMusic(m) }
            if (tempIndex == -1) {
                tempIndex = this.findIndexLast(index + 1, this.size) { m: Music, _ -> !IgnoreMusic.isIgnoreMusic(m) }
            }
        }
        return tempIndex
    }

    /**
     * 获取下一首
     * @return 如果有音乐则返回下标，如果没有则返回-1
     */
    fun nextIndex(): Int {
        if (this.isEmpty()) return -1
        val index: Int = if (this.index + 1 >= this.size) 0 else this.index + 1
        var tempIndex = index
        if (config.musicOrigin == PlayerConfig.MusicOrigin.LOCAL) {
            tempIndex = this.findIndexFirst(index, this.size) { m: Music, _: Int -> !IgnoreMusic.isIgnoreMusic(m) }
            if (tempIndex == -1) {
                tempIndex = this.findIndexFirst(0, index - 1) { m: Music, _: Int -> !IgnoreMusic.isIgnoreMusic(m) }
            }
        }
        return tempIndex
    }

    fun changeToLocal() {
        index = localIndex
        clear()
        addAll(localList)
    }

    fun reset() {
        index = -1
    }

    override fun onMusicOriginChanged(origin: PlayerConfig.MusicOrigin) {
        when (origin) {
            PlayerConfig.MusicOrigin.LOCAL -> {
                index = localIndex
                clear()
                addAll(localList)
            }
            PlayerConfig.MusicOrigin.WAIT -> {
                index = 0
                clear()
            }
            PlayerConfig.MusicOrigin.STORAGE -> {
                index = 0
                clear()
            }
            PlayerConfig.MusicOrigin.INTERNET -> {
            }
        }
    }
}