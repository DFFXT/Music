package com.web.moudle.lyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.BitmapDrawable
import android.media.audiofx.Visualizer
import android.os.IBinder
import android.view.View
import androidx.core.content.FileProvider
import com.mpatric.mp3agic.Mp3File
import com.music.m.BuildConfig
import com.web.common.base.*
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
import com.web.misc.imageDraw.WaveDraw
import com.web.moudle.lyrics.bean.LyricsLine
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.SongSheetManager
import com.web.moudle.music.player.other.IMusicControl
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.music.player.plug.ActionControlPlug
import com.web.moudle.service.FileDownloadService
import com.web.moudle.setting.lyrics.LyricsSettingActivity

import com.music.m.R
import kotlinx.android.synthetic.main.music_control_big.view.*
import kotlinx.android.synthetic.main.music_lyrics_view.*
import kotlinx.android.synthetic.main.music_lyrics_view.rootView
import kotlinx.android.synthetic.main.music_lyrics_view.topBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.io.File
import kotlin.Exception

@ObsoleteCoroutinesApi
class LyricsActivity : BaseActivity() {
    private var connect: IMusicControl? = null
    private var visualizer: Visualizer? = null
    private val list = ArrayList<LyricsLine>()
    private var lyricsPlug: LyricsSearchPlug? = null
    private var commentDialog: CommentDialog? = null
    private var connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            connect = (service as? IMusicControl)?:return
            connect?.addObserver(this@LyricsActivity, observer)
            connect?.getPlayerInfo(this@LyricsActivity)

            visualizer = Visualizer(connect?.getMediaSessionId()!!)
            visualizer!!.captureSize = Visualizer.getCaptureSizeRange()[0]
            visualizer!!.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            visualizer!!.setDataCaptureListener(
                object : Visualizer.OnDataCaptureListener {
                    override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
                    }

                    override fun onWaveFormDataCapture(visualizer: Visualizer, waveform: ByteArray, samplingRate: Int) {
                        waveDraw.byteArray = waveform
                    }
                },
                Visualizer.getMaxCaptureRate(), true, true
            )
            visualizer!!.enabled = true
        }
    }
    private var immediatelyShow = true
    private var canScroll = true
    private var rotation = 0f
    private val waveDraw = WaveDraw()
    private var colorList: IntArray? = null
    // **dispatchers一定要是main不能是default，不然会导致过一段时间整个应用无法点击，类似于卡死
    private val tick = Ticker(30, 0, Dispatchers.Main) {
        rotation += 1f
        if (rotation> 360) {
            rotation -= 360f
        }
        iv_artistIcon.rotation = rotation
    }
    private var observer: PlayerObserver = object : PlayerObserver() {

        override fun onLoad(music: Music?, maxTime: Int) {
            immediatelyShow = true
            val bitmap =PlayerConfig.bitmap ?: ResUtil.getBitmapFromResoucs(R.drawable.singer_default_icon)
            val mBitmap = bitmap.copy(bitmap.config, false)
            iv_artistIcon.setImageBitmap(bitmap)
            rootView.background = BitmapDrawable(resources, ImageLoad.buildBlurBitmap(mBitmap, 14f))

            layout_musicControl.iv_love.isSelected = music?.isLike ?: false
            if (music is InternetMusicForPlay) {
                layout_musicControl.card_love.alpha = 0.5f
                layout_musicControl.card_love.cardElevation = 0f
            } else {
                layout_musicControl.card_love.alpha = 1f
                layout_musicControl.card_love.cardElevation = ViewUtil.dpToPx(2f).toFloat()
            }
            if (!music?.song_id.isNullOrEmpty()) {
                card_comment.visibility = View.VISIBLE
            } else {
                card_comment.visibility = View.GONE
            }
            loadLyrics(music)
            onPlay()
        }

        override fun onPlay() {
            layout_musicControl.iv_play.setImageResource(R.drawable.icon_play_white)
            tick.start()
        }

        override fun onPause() {
            layout_musicControl.iv_play.setImageResource(R.drawable.icon_pause_white_fill)
            tick.stop()
        }

        override fun onCurrentTime(duration: Int, maxTime: Int) {
            if (immediatelyShow) { // **进入activity时需要立即同步
                lv_lyrics.setCurrentTimeImmediately(duration)
                immediatelyShow = false
            } else {
                lv_lyrics.setCurrentTime(duration)
            }
        }

        override fun onPlayTypeChanged(playType: PlayerConfig.PlayType?) {
            layout_musicControl.iv_playType.setImageResource(
                when (playType) {
                    PlayerConfig.PlayType.ALL_LOOP -> R.drawable.music_type_all_loop
                    PlayerConfig.PlayType.ONE_LOOP -> R.drawable.music_type_one_loop
                    PlayerConfig.PlayType.ALL_ONCE -> R.drawable.music_type_all_once
                    PlayerConfig.PlayType.ONE_ONCE -> R.drawable.music_type_one_once
                    PlayerConfig.PlayType.RANDOM -> R.drawable.random_icon
                    else -> R.drawable.random_icon
                }
            )
        }

        override fun onMusicOriginChanged(origin: PlayerConfig.MusicOrigin?) {
            if (origin == PlayerConfig.MusicOrigin.INTERNET) {
                card_download.visibility = View.VISIBLE
            } else {
                card_download.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        immediatelyShow = true
        connect?.getPlayerInfo(this)
        super.onResume()
    }

    override fun enableSwipeToBack(): Boolean = true

    override fun getLayoutId(): Int = R.layout.music_lyrics_view

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        riv_wave.afterDraw = waveDraw
        lv_lyrics.textColor = LyricsSettingActivity.getLyricsColor()
        lv_lyrics.setTextSize(LyricsSettingActivity.getLyricsSize().toFloat())
        lv_lyrics.setTextFocusColor(LyricsSettingActivity.getLyricsFocusColor())
        lv_lyrics.lyrics = list
        topBar.setEndImageListener(
            View.OnClickListener {
                if (canScroll) {
                    canScroll = false
                    topBar.setEndImage(R.drawable.locked)
                } else {
                    canScroll = true
                    topBar.setEndImage(R.drawable.unlock)
                }
                lv_lyrics.setCanScroll(canScroll)
            }
        )

        card_comment.setOnClickListener {
            if (commentDialog == null) {
                commentDialog = CommentDialog(this)
            }
            commentDialog?.show()
        }

        card_download.setOnClickListener {
            val m = (PlayerConfig.music ?: return@setOnClickListener) as? InternetMusicForPlay
                ?: return@setOnClickListener
            val im = InternetMusicDetail(
                m.song_id,
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
            FileDownloadService.addTask(this, im)
        }

        layout_musicControl.iv_playType.setOnClickListener {
            connect?.changePlayType(PlayerConfig.playType.next())
        }
        layout_musicControl.iv_love.setOnClickListener {
            val m = PlayerConfig.music ?: return@setOnClickListener
            if (m is InternetMusicForPlay)return@setOnClickListener
            if (m.isLike) {
                SongSheetManager.removeLike(m)
            } else {
                SongSheetManager.setAsLike(m)
            }
            layout_musicControl.iv_love.isSelected = m.isLike
        }

        layout_musicControl.next.setOnClickListener {
            connect?.next(false)
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

        iv_setting.setOnClickListener {
            toggleSettingBox()
        }

        iv_sizeIncrease.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize() + 2)
        }
        iv_sizeDecrease.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize() - 2)
        }
        iv_lyricsBg.setOnClickListener {
            val color = LyricsSettingActivity.getLyricsColor()
            val nextColor = getNextLyricsColor(color)
            LyricsSettingActivity.setLyricsColor(nextColor)
            lv_lyrics.textColor = nextColor
        }
        iv_lyricsFore.setOnClickListener {
            val color = LyricsSettingActivity.getLyricsFocusColor()
            val nextColor = getNextLyricsColor(color)
            LyricsSettingActivity.setLyricsFocusColor(nextColor)
            lv_lyrics.setTextFocusColor(nextColor)
        }
        iv_lyricsSelect.setOnClickListener {
            iv_searchLyrics.performClick()
        }
        iv_share.setOnClickListener {
            if (connect == null)return@setOnClickListener
            val intent = Intent(Intent.ACTION_SEND)

            val music = PlayerConfig.music
            if (PlayerConfig.musicOrigin == PlayerConfig.MusicOrigin.INTERNET) {
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, music!!.path)
            } else {
                intent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, File(PlayerConfig.music!!.path))
                )
                intent.type = "video/*"
            }
            startActivity(Intent.createChooser(intent, PlayerConfig.music?.musicName + " - 分享"))
        }

        iv_searchLyrics.setOnClickListener {
            if (PlayerConfig.music == null)return@setOnClickListener
            if (lyricsPlug == null) {
                lyricsPlug = LyricsSearchPlug(this, connect!!)
            }
            lyricsPlug?.showCenter(rootView, PlayerConfig.music!!)
        }

        intent = Intent(this, NewPlayer::class.java)
        intent.action = ActionControlPlug.BIND
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
    private fun getNextLyricsColor(currentColor: Int): Int {
        var index = 0
        if (colorList == null) colorList = ResUtil.getIntArray(R.array.lyricsColorArray)
        colorList!!.forEachIndexed { mIndex, c ->
            if (c == currentColor) {
                index = mIndex + 1
                if (index >= colorList!!.size) {
                    index = 0
                }
                return@forEachIndexed
            }
        }
        return colorList!![index]
    }
    private fun setLyricsSize(size: Int) {
        val max = ResUtil.getSize(R.dimen.textSize_large)
        val min = ResUtil.getSize(R.dimen.textSize_min)
        val mSize = when {
            size <min -> min
            size> max -> max
            else -> size
        }
        LyricsSettingActivity.setLyricsSize(mSize)
        lv_lyrics.setTextSize(mSize.toFloat())
    }
    private fun toggleSettingBox() {
        val duration = 300
        val lp = layout_setting.layoutParams
        if (layout_setting.height == 0) {
            // **加号不能放在第二行
            val height = layout_setting.childCount * ViewUtil.dpToFloatPx(26f) + (layout_setting.childCount + 1) * ViewUtil.dpToFloatPx(6f) + layout_setting.childCount
            ViewUtil.animator(
                layout_setting, 0, height.toInt(), duration,
                {
                    lp.height = it.animatedValue as Int
                    layout_setting.layoutParams = lp
                },
                null
            )
        } else {
            ViewUtil.animator(
                layout_setting, layout_setting.height, 0, duration,
                {
                    lp.height = it.animatedValue as Int
                    layout_setting.layoutParams = lp
                },
                null
            )
        }
    }

    private fun loadLyrics(music: Music?) { // --设置歌词内容
        if (music == null) return
        topBar.setMainTitle(music.musicName)
        list.clear()
        if (Shortcut.fileExsist(music.lyricsPath)) { // ---存在歌词
            val lyricsAnalysis = LyricsAnalysis(GetFiles().readText(music.lyricsPath))
            list.addAll(lyricsAnalysis.lyrics)
        } else { // **没找到歌词
            try {
                val mp3 = Mp3File(music.path)
                if (mp3.hasId3v2Tag()) {
                    val lyrics = mp3.id3v2Tag.lyrics
                    if (!lyrics.isNullOrEmpty()) {
                        val lyricsAnalysis = LyricsAnalysis(lyrics)
                        list.addAll(lyricsAnalysis.lyrics)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (list.isNotEmpty()) {
            iv_searchLyrics.visibility = View.GONE
        } else {
            iv_searchLyrics.visibility = View.VISIBLE
        }
        lv_lyrics!!.lyrics = list
    }

    override fun onDestroy() {
        tick.stop()
        unbindService(connection)
        visualizer?.release()
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun actionStart(ctx: Context) {
            ctx.startActivity(Intent(ctx, LyricsActivity::class.java))
        }
    }
}