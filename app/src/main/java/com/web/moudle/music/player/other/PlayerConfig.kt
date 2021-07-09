package com.web.moudle.music.player.other

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mpatric.mp3agic.Mp3File
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
            setBitmapPath(music?.singer)
        }
    }

    private fun setBitmapPath(singerName: String?) {
        try {
            val file = File(GetFiles.singerPath + singerName + ".png")
            if (file.exists() && file.isFile) { //---存在图片
                val fis = FileInputStream(file)
                bitmap = BitmapFactory.decodeStream(fis)
            }else{
                tryGetBitmap()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun tryGetBitmap(){
        music?.let {
            try {
                val mp3 = Mp3File(it.path)
                if (mp3.hasId3v2Tag()){
                    val img = mp3.id3v2Tag.albumImage
                    if (img != null){
                        bitmap = BitmapFactory.decodeByteArray(img,0,img.size)
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }

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