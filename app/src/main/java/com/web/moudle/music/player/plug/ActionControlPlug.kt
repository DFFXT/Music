package com.web.moudle.music.player.plug

import android.content.Context
import android.content.Intent
import androidx.annotation.UiThread
import com.web.app.MyApplication
import com.web.common.constant.AppConfig
import com.web.common.tool.MToast.showToast
import com.web.common.util.MediaQuery
import com.web.common.util.MediaQuery.getLocalList
import com.web.common.util.MediaQuery.scanMedia
import com.web.common.util.ResUtil
import com.web.data.InternetMusicForPlay
import com.web.data.Music
import com.web.data.MusicList
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.core.IPlayer
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.MusicDataSource
import com.web.moudle.music.player.other.PlayInterfaceManager
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plugInterface.IntentReceiver
import com.web.web.R
import org.litepal.crud.DataSupport
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ActionControlPlug(
    private val control: IMusicControl,
    private val player: IPlayer,
    private val playInterfaceManager: PlayInterfaceManager,
    private val dataSource: MusicDataSource
) : IntentReceiver {
    private val playTypePlug = PlayTypePlug(control, player, dataSource)
    override fun dispatch(intent: Intent) {
        when (intent.action) {
            ACTION_NEXT ->
                control.next(false)
            ACTION_PRE ->
                control.pre()
            ACTION_STATUS_CHANGE -> {
                control.changePlayerPlayingStatus()
            }
            ACTION_SCAN -> {
                scanMusicMedia()
            }
            ACTION_ClEAR_ALL_MUSIC -> {
                DataSupport.deleteAll(Music::class.java)
                getMusicList()
                reset()
            }
            ACTION_DOWNLOAD_COMPLETE -> {
                val music = DataSupport.where(
                    "path=?",
                    intent.getStringExtra("path")
                ).findFirst(Music::class.java)
                dataSource.localList.add(music)
                dataSource.sort()
                musicListChange()
            }
            /*ACTION_PLAY_LOCAL_MUSIC -> {
                player.config.musicOrigin = PlayerConfig.MusicOrigin.LOCAL
                control.play(intent.getSerializableExtra(COMMAND_SEND_SINGLE_DATA) as Music)
            }*/
            ACTION_PLAY_INTERNET_MUSIC -> {
                /*player.config.musicOrigin = PlayerConfig.MusicOrigin.INTERNET
                control.play(intent.getSerializableExtra(COMMAND_SEND_SINGLE_DATA) as InternetMusicForPlay)*/
            }
        }
    }

    private val gettingMusicLock: Lock = ReentrantLock()
    private val scanningMusicLock: Lock = ReentrantLock()

    @UiThread
    private fun scanMusicMedia() {
        if (!scanningMusicLock.tryLock()) return
        scanMedia(MyApplication.context) { isOk: Boolean ->
            if (isOk) {
                getMusicList()
                showToast(MyApplication.context, ResUtil.getString(R.string.scanOver))
            } else {
                showToast(MyApplication.context, ResUtil.getString(R.string.scanOver_noMusic))
                musicListChange()
            }
            scanningMusicLock.unlock()
        }
    }

    /**
     * 获取本地列表
     */
    @UiThread
    private fun getMusicList() {
        if (!gettingMusicLock.tryLock()) return
        getLocalList { res: ArrayList<MusicList<Music>> ->
            dataSource.localList.clear()
            dataSource.localList.addAll(res[0])
            dataSource.sort()
            dataSource.clear()
            dataSource.addAll(dataSource.localList)
            if (dataSource.size == 0 && AppConfig.noNeedScan) { // **没有扫描媒体库
                scanMusicMedia()
            } else {
                musicListChange()
                gettingMusicLock.unlock()
            }
        }
    }

    private fun musicListChange() {
        if (control.getCurrentMusic() == null) {
            val index: Int = dataSource.indexOfFirst { m: Music -> m.path == AppConfig.lastMusic }
            if (index >= 0) {
                PlayerConfig.setMusic(dataSource[index])
                dataSource.addMusic(dataSource[index], PlayerConfig.MusicOrigin.LOCAL)
                control.loadMusic(dataSource[index], false)
            }
        }
        playTypePlug.randomSystem.reset(4)
        playTypePlug.addIntRangeWithFilter(0, dataSource.size, dataSource)
        // randomSystem.addIntRange(0,musicList.get(groupIndex).size());
        playInterfaceManager.onMusicListChange(dataSource)
        PlayerConfig.music?.let {
            if (control.getCurrentMusic() == null) {
                control.loadMusic(it, false)
            }
            if (player.isPlaying()) {
                playInterfaceManager.onPlay()
            } else {
                playInterfaceManager.onPause()
            }
        }
    }

    private fun deleteMusic(deleteFile: Boolean, group: Int, vararg childList: Int) {
        val deleteList: MutableList<Music> = ArrayList()
        for (child in childList) {
            val music: Music = dataSource.localList[child]
            deleteList.add(music)
            if (music === PlayerConfig.music) reset()
            MediaQuery.deleteMusic(MyApplication.context, music, group, deleteFile)
        }
        for (m in deleteList) {
            dataSource.remove(m)
        }
        getMusicList()
    }

    private fun reset() {
        player.reset()
        playInterfaceManager.onLoad(null, 0)
        playInterfaceManager.onCurrentTime(0, 0)
        playInterfaceManager.onMusicOriginChanged(PlayerConfig.MusicOrigin.LOCAL)
        playInterfaceManager.onPause()
        PlayerConfig.musicOrigin = PlayerConfig.MusicOrigin.LOCAL
        PlayerConfig.setMusic(null)
        dataSource.reset()
    }
    companion object {

        const val BIND = "BIND"
        const val ACTION_SCAN = "ACTION_SCAN"
        const val ACTION_DELETE = "ACTION_DELETE"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PRE = "ACTION_PRE"
        const val ACTION_ClEAR_ALL_MUSIC = "ACTION_ClEAR_ALL_MUSIC"
        const val ACTION_STATUS_CHANGE = "ACTION_STATUS_CHANGE"
        const val COMMAND_SEND_SINGLE_DATA = "COMMAND_SEND_SINGLE_DATA"
        const val ACTION_PLAY_INTERNET_MUSIC = "ACTION_PLAY_INTERNET_MUSIC"
        const val ACTION_PLAY_LOCAL_MUSIC = "ACTION_PLAY_LOCAL_MUSIC"
        const val ACTION_FLOAT_WINDOW_CHANGE = "ACTION_FLOAT_WINDOW_CHANGE"
        const val ACTION_DOWNLOAD_COMPLETE = "ACTION_DOWNLOAD_COMPLETE"
        private const val EXTRA1 = "EXTRA1"

        @JvmStatic
        fun clear(ctx: Context) {
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.action = ACTION_ClEAR_ALL_MUSIC
            ctx.startService(intent)
        }

        @JvmStatic
        fun scan(context: Context) {
            val intent = Intent(context, NewPlayer::class.java)
            intent.action = ACTION_SCAN
            context.startService(intent)
        }
        @JvmStatic
        fun delete(context: Context, music: Music, deleteFile: Boolean) {
            val intent = Intent(context, NewPlayer::class.java)
            intent.action = ACTION_DELETE
            intent.putExtra(COMMAND_SEND_SINGLE_DATA, music)
            intent.putExtra(EXTRA1, deleteFile)
            context.startService(intent)
        }

        /**
         * 播放网络音乐
         *
         * @param ctx   context
         * @param music music
         */
        @JvmStatic
        fun play(ctx: Context, music: InternetMusicForPlay?) {
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.putExtra(COMMAND_SEND_SINGLE_DATA, music)
            intent.action = ACTION_PLAY_INTERNET_MUSIC
            ctx.startService(intent)
        }
        @JvmStatic
        fun play(ctx: Context, music: Music?) {
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.putExtra(COMMAND_SEND_SINGLE_DATA, music)
            intent.action = ACTION_PLAY_LOCAL_MUSIC
            ctx.startService(intent)
        }

        @JvmStatic
        fun floatWindowChange(ctx: Context) {
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.action = ACTION_FLOAT_WINDOW_CHANGE
            ctx.startService(intent)
        }

        @JvmStatic
        fun musicDownloadComplete(ctx: Context, path: String?) {
            val intent = Intent(ctx, NewPlayer::class.java)
            intent.action = ACTION_DOWNLOAD_COMPLETE
            intent.putExtra("path", path)
            ctx.startService(intent)
        }
    }
}