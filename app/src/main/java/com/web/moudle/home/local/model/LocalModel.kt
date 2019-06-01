package com.web.moudle.home.local.model

import com.web.data.InternetMusicDetail
import com.web.data.Music
import com.web.data.RecentPlayMusic
import com.web.moudle.music.player.SongSheetManager
import com.web.moudle.musicDownload.bean.DownloadMusic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.litepal.crud.DataSupport

class LocalModel {

    fun getMusicNum(callback: ((Int) -> Unit)) {
        GlobalScope.launch(Dispatchers.IO) {
            val num=DataSupport.count(Music::class.java)
            withContext(Dispatchers.Main) {
                callback(num)
            }
        }
    }

    fun getPreferNum(callback: (Int) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val sheets = SongSheetManager.getSongSheetList().songList
            var preferNum = 0
            if (sheets.size != 0) {
                preferNum = sheets[0].size()
            }
            withContext(Dispatchers.Main) {
                callback(preferNum)
            }

        }
    }

    fun getDownloadNum(callback: (Int) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val num=DataSupport.count(InternetMusicDetail::class.java)
            withContext(Dispatchers.Main) {
                callback(num)
            }
        }
    }

    fun getRecentMusicNum(callback: (Int) -> Unit){
        GlobalScope.launch(Dispatchers.IO) {
            val num=DataSupport.count(RecentPlayMusic::class.java)
            withContext(Dispatchers.Main) {
                callback(num)
            }
        }
    }
}