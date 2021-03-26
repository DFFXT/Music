package com.web.moudle.music.player.other

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.web.config.GetFiles
import com.web.data.Music
import java.io.File
import java.io.FileInputStream

class PlayerConfig(private val playInterfaceManager: PlayInterfaceManager) {
    //***播放模式
    var playType = PlayType.ALL_LOOP
    var musicOrigin = MusicOrigin.LOCAL
        set(value) {
            if (field != value){
                field = value
                playInterfaceManager.onMusicOriginChanged(value)
            }
        }
    var music: Music? = null
        private set
    var bitmap: Bitmap? = null
    var isHasInit = false
    var isPrepared = false
    fun setMusic(music: Music?) {
        if (this.music !== music) {
            this.music = music
            bitmap = null
        }
    }

    fun setBitmapPath(singerName: String) {
        try {
            val file = File(GetFiles.singerPath + singerName + ".png")
            if (file.exists() && file.isFile) { //---存在图片
                val fis = FileInputStream(file)
                bitmap = BitmapFactory.decodeStream(fis)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    enum class PlayType {
        ONE_LOOP, ALL_LOOP, ALL_ONCE, ONE_ONCE, RANDOM;

        operator fun next(): PlayType {
            var type: PlayType? = null
            type = when (this) {
                ALL_LOOP -> ALL_ONCE
                ALL_ONCE -> ONE_LOOP
                ONE_LOOP -> ONE_ONCE
                ONE_ONCE -> RANDOM
                RANDOM -> ALL_LOOP
            }
            return type
        }
    }

    enum class MusicOrigin {
        LOCAL, INTERNET, WAIT, STORAGE
    }
}