package com.web.moudle.lyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import com.web.common.base.BaseActivity
import com.web.common.util.ResUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.config.Shortcut
import com.web.data.Music
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicEntry.ui.PlayerObserver
import com.web.web.R
import kotlinx.android.synthetic.main.music_lyrics_view.*
import java.util.*

class LyricsActivity : BaseActivity() {
    private var connect: MusicPlay.Connect? = null
    private val list = ArrayList<LyricsLine>()
    private var connection: ServiceConnection? = null
    private var actionStart = true
    private var canScroll=true
    private var observer: PlayerObserver = object : PlayerObserver() {

        override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
            actionStart = true
            loadLyrics(music)
        }

        override fun currentTime(group: Int, child: Int, time: Int) {
            if (actionStart) {
                lv_lyrics.setCurrentTimeImmediately(time)
                actionStart = false
            } else {
                lv_lyrics.setCurrentTime(time)
            }
        }
    }

    override fun enableSwipeToBack(): Boolean =true

    override fun getLayoutId(): Int = R.layout.music_lyrics_view

    override fun initView() {

        lv_lyrics!!.textColor = ResUtil.getColor(R.color.themeColor)
        lv_lyrics!!.lyrics = list
       topBar.setEndImageListener(View.OnClickListener {
           if (canScroll) {
               canScroll= false
               topBar.setEndImage(R.drawable.locked)
           } else {
               canScroll=true
               topBar.setEndImage(R.drawable.unlock)
           }
           lv_lyrics!!.setCanScroll(canScroll)
        })
        lv_lyrics!!.setSeekListener { seekTo ->
            connect?.seekTo(seekTo)
            true
        }

        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {

            }

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                connect = service as MusicPlay.Connect
                connect?.addObserver(this@LyricsActivity, observer)
                connect?.getPlayerInfo()
            }
        }
        intent = Intent(this, MusicPlay::class.java)
        intent.action = MusicPlay.BIND
        bindService(intent, connection!!, Context.BIND_AUTO_CREATE)


    }


    private fun loadLyrics(music: Music?) {//--设置歌词内容
        if (music == null) return
        topBar.setMainTitle(music.musicName)
        list.clear()
        if (Shortcut.fileExsist(music.lyricsPath)) {//---存在歌词
            val lyricsAnalysis = LyricsAnalysis(GetFiles().readText(music.lyricsPath))
            list.addAll(lyricsAnalysis.lyrics)
        } else {//**没找到歌词
            val line = LyricsLine()
            line.time = 0
            line.line = ResUtil.getString(R.string.lyrics_noLyrics)
            list.add(line)
        }
        lv_lyrics!!.lyrics = list
    }


    override fun onDestroy() {
        connection?.let {
            unbindService(it)
        }
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun actionStart(ctx: Context) {
            ctx.startActivity(Intent(ctx, LyricsActivity::class.java))
        }
    }


}