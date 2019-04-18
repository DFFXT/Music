package com.web.moudle.lyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.BitmapDrawable
import android.media.audiofx.Visualizer
import android.os.IBinder
import android.view.View
import com.web.common.base.BaseActivity
import com.web.common.base.PlayerObserver
import com.web.common.base.log
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.Ticker
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.config.GetFiles
import com.web.config.LyricsAnalysis
import com.web.config.Shortcut
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicForPlay
import com.web.data.Music
import com.web.data.PlayerConfig
import com.web.misc.imageDraw.WaveDraw
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.music.player.SongSheetManager
import com.web.moudle.service.FileDownloadService
import com.web.moudle.setting.lyrics.LyricsSettingActivity
import com.web.web.R
import kotlinx.android.synthetic.main.music_control_big.view.*
import kotlinx.android.synthetic.main.music_lyrics_view.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.util.*

@ObsoleteCoroutinesApi
class LyricsActivity : BaseActivity() {
    private var connect: MusicPlay.Connect? = null
    private var visualizer:Visualizer?=null
    private val list = ArrayList<LyricsLine>()
    private var connection: ServiceConnection? = null
    private var actionStart = true
    private var canScroll=true
    private var rotation=0f
    private val waveDraw=WaveDraw()
    private val tick=Ticker(20,0){
        rotation+=1f
        if(rotation>360){
            rotation -= 360f
        }
        iv_artistIcon.rotation=rotation

    }
    private var observer: PlayerObserver = object : PlayerObserver() {

        override fun load(groupIndex: Int, childIndex: Int, music: Music?, maxTime: Int) {
            actionStart = true
            val bitmap=connect?.config?.bitmap?:ResUtil.getBitmapFromResoucs(R.drawable.singer_default_icon)
            val mBitmap=bitmap.copy(bitmap.config,false)
            iv_artistIcon.setImageBitmap(bitmap)
            rootView.background=BitmapDrawable(resources,ImageLoad.buildBlurBitmap(mBitmap,14f))

            layout_musicControl.iv_love.isSelected=music?.isLike?:false
            if(music is InternetMusicForPlay){
                layout_musicControl.card_love.alpha=0.5f
                layout_musicControl.card_love.cardElevation=0f
            }else{
                layout_musicControl.card_love.alpha=1f
                layout_musicControl.card_love.cardElevation=ViewUtil.dpToPx(2f).toFloat()
            }
            loadLyrics(music)
            play()
        }

        override fun play() {
            layout_musicControl.iv_play.setImageResource(R.drawable.icon_play_white)
            tick.start()
        }

        override fun pause() {
            layout_musicControl.iv_play.setImageResource(R.drawable.icon_pause_white_fill)
            tick.stop()
        }

        override fun currentTime(group: Int, child: Int, time: Int) {
            if (actionStart) {//**进入activity时需要立即同步
                lv_lyrics.setCurrentTimeImmediately(time)
                actionStart = false
            } else {
                lv_lyrics.setCurrentTime(time)
            }

        }

        override fun playTypeChanged(playType: PlayerConfig.PlayType?) {
            layout_musicControl.iv_playType.setImageResource(when(playType){
                PlayerConfig.PlayType.ALL_LOOP->R.drawable.music_type_all_loop
                PlayerConfig.PlayType.ONE_LOOP->R.drawable.music_type_one_loop
                PlayerConfig.PlayType.ALL_ONCE->R.drawable.music_type_all_once
                PlayerConfig.PlayType.ONE_ONCE->R.drawable.music_type_one_once
                else -> R.drawable.music_type_all_loop
            })
        }

        override fun musicOriginChanged(origin: PlayerConfig.MusicOrigin?) {
            if(origin==PlayerConfig.MusicOrigin.INTERNET){
                card_download.visibility=View.VISIBLE
            }else{
                card_download.visibility=View.GONE
            }
        }
    }

    override fun enableSwipeToBack(): Boolean =true

    override fun getLayoutId(): Int = R.layout.music_lyrics_view

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        riv_wave.afterDraw=waveDraw

        lv_lyrics.setClipPaddingTop(ViewUtil.dpToPx(30f))
        lv_lyrics.textColor = LyricsSettingActivity.getLyricsColor()
        lv_lyrics.setTextSize(LyricsSettingActivity.getLyricsSize().toFloat())
        lv_lyrics.setTextFocusColor(LyricsSettingActivity.getLyricsFocusColor())
        lv_lyrics.lyrics = list
        topBar.setEndImageListener(View.OnClickListener {
            if (canScroll) {
                canScroll= false
                topBar.setEndImage(R.drawable.locked)
            } else {
                canScroll=true
                topBar.setEndImage(R.drawable.unlock)
            }
            lv_lyrics.setCanScroll(canScroll)
        })


        card_download.setOnClickListener {
            val m= (connect?.config?.music ?: return@setOnClickListener) as? InternetMusicForPlay
                    ?: return@setOnClickListener
            val im=InternetMusicDetail(
                    "",
                    m.musicName,
                    m.singer,
                    null,
                    m.album,
                    m.duration,
                    m.size,
                    m.lrcLink,
                    m.path,
                    m.imgAddress,
                    m.suffix
            )
            FileDownloadService.addTask(this,im)
        }


        layout_musicControl.iv_playType.setOnClickListener {
            connect?.changePlayType()
        }
        layout_musicControl.iv_love.setOnClickListener {
            val m= connect?.config?.music ?: return@setOnClickListener
            if(m is InternetMusicForPlay)return@setOnClickListener
            if (m.isLike) {
                SongSheetManager.removeLike(m)
            } else {
                SongSheetManager.setAsLike(m)
            }
            layout_musicControl.iv_love.isSelected=m.isLike
            connect?.refreshList()
        }

        layout_musicControl.next.setOnClickListener {
            connect?.next()
        }
        layout_musicControl.iv_play.setOnClickListener {
            connect?.changePlayerPlayingStatus()
        }
        layout_musicControl.pre.setOnClickListener {
            connect?.pre()
        }
        iv_musicEffect.setOnClickListener {
            EqualizerActivity.actionStart(this)
        }
        lv_lyrics.setSeekListener { seekTo ->
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


                visualizer=Visualizer(connect?.mediaPlayId!!)
                visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[0]
                visualizer!!.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
                visualizer!!.setDataCaptureListener(object :Visualizer.OnDataCaptureListener{
                    override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
                    }

                    override fun onWaveFormDataCapture(visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {
                        waveDraw.byteArray=waveform
                    }
                },Visualizer.getMaxCaptureRate(),true,true)
                visualizer!!.enabled=true
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
        tick.stop()
        connect?.removeObserver(observer)
        connection?.let {
            unbindService(it)
            visualizer?.release()
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