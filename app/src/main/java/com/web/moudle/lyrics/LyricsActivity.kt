package com.web.moudle.lyrics

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.drawable.BitmapDrawable
import android.media.audiofx.Visualizer
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.mpatric.mp3agic.Mp3File
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
import com.music.m.databinding.MusicLyricsViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.io.File
import kotlin.Exception

@ObsoleteCoroutinesApi
class LyricsActivity : BaseViewBindingActivity<MusicLyricsViewBinding>() {
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
        binding.ivArtistIcon.rotation = rotation
    }
    private var observer: PlayerObserver = object : PlayerObserver() {

        override fun onLoad(music: Music?, maxTime: Int) {
            immediatelyShow = true
            val bitmap =PlayerConfig.bitmap ?: ResUtil.getBitmapFromResoucs(R.drawable.singer_default_icon)
            val mBitmap = bitmap.copy(bitmap.config!!, false)
            binding.ivArtistIcon.setImageBitmap(bitmap)
            binding.rootView.background = BitmapDrawable(resources, ImageLoad.buildBlurBitmap(mBitmap, 14f))

            binding.layoutMusicControl.ivLove.isSelected = music?.isLike ?: false
            if (music is InternetMusicForPlay) {
                binding.layoutMusicControl.cardLove.alpha = 0.5f
                binding.layoutMusicControl.cardLove.cardElevation = 0f
            } else {
                binding.layoutMusicControl.cardLove.alpha = 1f
                binding.layoutMusicControl.cardLove.cardElevation = ViewUtil.dpToPx(2f).toFloat()
            }
            if (!music?.song_id.isNullOrEmpty()) {
                binding.cardComment.visibility = View.VISIBLE
            } else {
                binding.cardComment.visibility = View.GONE
            }
            loadLyrics(music)
            onPlay()
        }

        override fun onPlay() {
            binding.layoutMusicControl.ivPlay.setImageResource(R.drawable.icon_play_white)
            tick.start()
        }

        override fun onPause() {
            binding.layoutMusicControl.ivPlay.setImageResource(R.drawable.icon_pause_white_fill)
            tick.stop()
        }

        override fun onCurrentTime(duration: Int, maxTime: Int) {
            if (immediatelyShow) { // **进入activity时需要立即同步
                binding.lvLyrics.setCurrentTimeImmediately(duration)
                immediatelyShow = false
            } else {
                binding.lvLyrics.setCurrentTime(duration)
            }
        }

        override fun onPlayTypeChanged(playType: PlayerConfig.PlayType?) {
            binding.layoutMusicControl.ivPlayType.setImageResource(
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
                binding.cardDownload.visibility = View.VISIBLE
            } else {
                binding.cardDownload.visibility = View.GONE
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
        binding.rivWave.afterDraw = waveDraw
        binding.lvLyrics.textColor = LyricsSettingActivity.getLyricsColor()
        binding.lvLyrics.setTextSize(LyricsSettingActivity.getLyricsSize().toFloat())
        binding.lvLyrics.setTextFocusColor(LyricsSettingActivity.getLyricsFocusColor())
        binding.lvLyrics.lyrics = list
        binding.topBar.setEndImageListener(
            View.OnClickListener {
                if (canScroll) {
                    canScroll = false
                    binding.topBar.setEndImage(R.drawable.locked)
                } else {
                    canScroll = true
                    binding.topBar.setEndImage(R.drawable.unlock)
                }
                binding.lvLyrics.setCanScroll(canScroll)
            }
        )

        binding.cardComment.setOnClickListener {
            if (commentDialog == null) {
                commentDialog = CommentDialog(this)
            }
            commentDialog?.show()
        }

        binding.cardDownload.setOnClickListener {
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

        binding.layoutMusicControl.ivPlayType.setOnClickListener {
            connect?.changePlayType(PlayerConfig.playType.next())
        }
        binding.layoutMusicControl.ivLove.setOnClickListener {
            val m = PlayerConfig.music ?: return@setOnClickListener
            if (m is InternetMusicForPlay) return@setOnClickListener
            if (m.isLike) {
                SongSheetManager.removeLike(m)
            } else {
                SongSheetManager.setAsLike(m)
            }
            binding.layoutMusicControl.ivLove.isSelected = m.isLike
        }

        binding.layoutMusicControl.next.setOnClickListener {
            connect?.next(false)
        }
        binding.layoutMusicControl.ivPlay.setOnClickListener {
            connect?.changePlayerPlayingStatus()
        }
        binding.layoutMusicControl.pre.setOnClickListener {
            connect?.pre()
        }
        binding.ivMusicEffect.setOnClickListener {
            EqualizerActivity.actionStart(this)
        }
        binding.lvLyrics.setSeekListener { seekTo ->
            connect?.seekTo(seekTo)
            true
        }

        binding.ivSetting.setOnClickListener {
            toggleSettingBox()
        }

        binding.ivSizeIncrease.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize() + 2)
        }
        binding.ivSizeDecrease.setOnClickListener {
            setLyricsSize(LyricsSettingActivity.getLyricsSize() - 2)
        }
        binding.ivLyricsBg.setOnClickListener {
            val color = LyricsSettingActivity.getLyricsColor()
            val nextColor = getNextLyricsColor(color)
            LyricsSettingActivity.setLyricsColor(nextColor)
            binding.lvLyrics.textColor = nextColor
        }
        binding.ivLyricsFore.setOnClickListener {
            val color = LyricsSettingActivity.getLyricsFocusColor()
            val nextColor = getNextLyricsColor(color)
            LyricsSettingActivity.setLyricsFocusColor(nextColor)
            binding.lvLyrics.setTextFocusColor(nextColor)
        }
        binding.ivLyricsSelect.setOnClickListener {
            binding.ivSearchLyrics.performClick()
        }
        binding.ivShare.setOnClickListener {
            if (connect == null) return@setOnClickListener
            val intent = Intent(Intent.ACTION_SEND)

            val music = PlayerConfig.music
            if (PlayerConfig.musicOrigin == PlayerConfig.MusicOrigin.INTERNET) {
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, music!!.path)
            } else {
                intent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(this, packageName, File(PlayerConfig.music!!.path))
                )
                intent.type = "video/*"
            }
            startActivity(Intent.createChooser(intent, PlayerConfig.music?.musicName + " - 分享"))
        }

        binding.ivSearchLyrics.setOnClickListener {
            if (PlayerConfig.music == null) return@setOnClickListener
            if (lyricsPlug == null) {
                lyricsPlug = LyricsSearchPlug(this, connect!!)
            }
            lyricsPlug?.showCenter(binding.rootView, PlayerConfig.music!!)
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
        binding.lvLyrics.setTextSize(mSize.toFloat())
    }
    private fun toggleSettingBox() {
        val duration = 300
        val lp = binding.layoutSetting.layoutParams
        if (binding.layoutSetting.height == 0) {
            // **加号不能放在第二行
            val height = binding.layoutSetting.childCount * ViewUtil.dpToFloatPx(26f) + (binding.layoutSetting.childCount + 1) * ViewUtil.dpToFloatPx(6f) + binding.layoutSetting.childCount
            ViewUtil.animator(
                binding.layoutSetting, 0, height.toInt(), duration,
                {
                    lp.height = it.animatedValue as Int
                    binding.layoutSetting.layoutParams = lp
                },
                null
            )
        } else {
            ViewUtil.animator(
                binding.layoutSetting, binding.layoutSetting.height, 0, duration,
                {
                    lp.height = it.animatedValue as Int
                    binding.layoutSetting.layoutParams = lp
                },
                null
            )
        }
    }

    private fun loadLyrics(music: Music?) { // --设置歌词内容
        if (music == null) return
        binding.topBar.setMainTitle(music.musicName)
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
            binding.ivSearchLyrics.visibility = View.GONE
        } else {
            binding.ivSearchLyrics.visibility = View.VISIBLE
        }
        binding.lvLyrics!!.lyrics = list
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