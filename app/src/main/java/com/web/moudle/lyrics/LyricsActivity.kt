package com.web.moudle.lyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
    private var observer: PlayerObserver = object : PlayerObserver() {

        override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
            actionStart = true
            loadLyrics()
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

    override fun getLayoutId(): Int = R.layout.music_lyrics_view

    override fun initView() {
        //lv_lyrics.setShowLineAccount(16)
        lv_lyrics!!.textColor = ResUtil.getColor(R.color.themeColor)
        lv_lyrics!!.lyrics = list
        iv_lock.tag = false
        iv_lock.setImageResource(R.drawable.unlock)
        iv_lock.setOnClickListener {
            if (iv_lock.tag as Boolean) {
                iv_lock.tag = false
                iv_lock.setImageResource(R.drawable.unlock)
            } else {
                iv_lock.tag = true
                iv_lock.setImageResource(R.drawable.locked)
            }
            lv_lyrics!!.setCanScroll(!(iv_lock.tag as Boolean))
        }
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

        iv_back.setOnClickListener {
            finish()
        }

    }


    private fun loadLyrics() {//--设置歌词内容
        if (connect == null) return
        val music = connect!!.playingMusic ?: return
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