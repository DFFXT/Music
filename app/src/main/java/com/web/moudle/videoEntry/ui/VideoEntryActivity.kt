package com.web.moudle.videoEntry.ui

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.view.SurfaceHolder
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.web.common.base.BaseActivity
import com.web.common.base.MyApplication
import com.web.common.base.errorClickLinsten
import com.web.common.base.showError
import com.web.common.util.ResUtil
import com.web.common.util.WindowUtil
import com.web.config.Shortcut
import com.web.moudle.videoEntry.bean.VideoInfoBox
import com.web.moudle.videoEntry.model.VideoViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_video_entry.*
import kotlinx.android.synthetic.main.layout_video_control.view.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class VideoEntryActivity : BaseActivity() {
    private lateinit var videoId: String
    private lateinit var songId:String
    override fun getLayoutId(): Int = R.layout.activity_video_entry
    private lateinit var model: VideoViewModel
    private val FINISH = 1
    private val STAY = 2
    private var url: String? = null
    private var player:MediaPlayer= MediaPlayer()
    private var seekBarTouch=false

    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)
        WindowUtil.setStatusBarTextDark(window,false)
        topBar.setStartImageListener(View.OnClickListener {
            onBackPressed()
        })
        videoId = intent.getStringExtra(INTENT_DATA)
        songId = intent.getStringExtra(SONG_ID)
        model = ViewModelProviders.of(this)[VideoViewModel::class.java]
        model.videoInfo.observe(this, Observer<VideoInfoBox> {
            if (it == null) {
                vv_video.showError()
                return@Observer
            }
            if (it.fileInfo.source_path.contains("http://dispatcher.video.qiyi.com")) {
                val index = it.fileInfo.source_path.indexOf("?")
                VideoWebViewActivity.actionStartForResult(this, "http://m.iqiyi.com/shareplay.html?${it.fileInfo.source_path.substring(index + 1)}&operator=0&musicch=ppzs&fr=android&ver=6.9.1.0&cid=qc_100001_300089&coop=coop_baidump3&fullscreen=0&autoplay=1&source=&purl=", FINISH)
            } else {
                model.getVideoUrl(it.fileInfo.source_path)
            }
        })

        model.videoUrl.observe(this, Observer<String> {
            if (it == null) {
                vv_video.showError()
                return@Observer
            }
            url = it
            player.setDataSource(it)
            player.prepareAsync()
            //vv_video.setVideoURI(Uri.parse(it))
        })

        initVideoController()
        model.getVideoInfo(videoId = videoId,songId = songId)
    }

    private fun initVideoController() {
        vv_video.holder.addCallback(SurfaceViewCallback())
        frame_videoStatus.setOnClickListener { toggleShow() }
        vv_video.setOnClickListener { toggleShow() }
        iv_videoStatus.setOnClickListener {
            if (player.isPlaying) {
                videoPause()
            } else {
                videoStart()
                toggleShow()
            }
        }
        mc_videoController.bar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mc_videoController.tv_videoCurrentTime.text=ResUtil.timeFormat("mm:ss",seekBar.progress.toLong())
                if(fromUser){
                    preClickTime = System.currentTimeMillis()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekBarTouch=true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player.seekTo(seekBar.progress)
                seekBarTouch=false
                hideController()
            }
        })
        mc_videoController.iv_fullScreen.setOnClickListener {
            requestedOrientation = if(resources.configuration.orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }else{
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }

        player.setOnSeekCompleteListener {
            if(player.isPlaying)
                videoStart()
        }
        player.setOnPreparedListener {
            videoStart()
            toggleShow()
            mc_videoController.bar.max = it.duration
            val r = ResUtil.timeFormat("mm:ss", it.duration.toLong())
            mc_videoController.tv_videoMaxTime.text = r
            sizeChanged()
        }
        player.setOnCompletionListener {
            videoPause()
        }
        player.setOnBufferingUpdateListener { _, percent ->
            mc_videoController.bar.secondaryProgress=percent*mc_videoController.bar.max/100
        }
        player.setOnErrorListener { _, _, extra ->
            if (extra == -2147483648) {///***不知道为什么，荣耀meta10很多视频都不能播放，只能跳转的到webView中来播放
                val bitmap = ResUtil.getBitmapRotate(R.drawable.icon_back_black, 180f)
                vv_video.showError(getString(R.string.video_unknownError), BitmapDrawable(resources, bitmap))
                vv_video.errorClickLinsten = View.OnClickListener {
                    VideoWebViewActivity.actionStartForResult(this, url!!, STAY)
                }
            }
            true
        }
        player.setOnVideoSizeChangedListener { _, _, _ ->
            sizeChanged()
        }
    }

    /**
     * 视频大小切换
     */
    private fun sizeChanged(){
        val width=MyApplication.context.resources.displayMetrics.widthPixels
        val height=MyApplication.context.resources.displayMetrics.heightPixels
        val vvW: Int
        val vvH: Int
        if(width<height){//**横屏
            vvW=width
            vvH=player.videoHeight*width/player.videoWidth
        }else{
            vvH=height
            vvW=player.videoWidth*height/player.videoHeight
        }
        val lp=vv_video.layoutParams
        lp.width=vvW
        lp.height=vvH
        vv_video.layoutParams=lp
    }


    private fun videoStart() {
        iv_videoStatus.setImageResource(R.drawable.icon_video_play)
        player.start()
        run()
    }

    private fun videoPause() {
        position=player.currentPosition
        iv_videoStatus.setImageResource(R.drawable.icon_video_pause)
        player.pause()
    }

    private var preClickTime = 0L
    private fun toggleShow(forceShow:Boolean?=false) {
        if (g_controlGroup.visibility != View.VISIBLE) {
            g_controlGroup.visibility = View.VISIBLE
            preClickTime = System.currentTimeMillis()
            hideController()
        } else {
            g_controlGroup.visibility = View.GONE
        }


    }
    private fun hideController(){
        mc_videoController.postDelayed({
            if (System.currentTimeMillis() - preClickTime >= 3500&&!seekBarTouch) {
                g_controlGroup.visibility = View.GONE
            }
        }, 3500)
    }


    private var position=0
    private val execute = ThreadPoolExecutor(1, 1, 10, TimeUnit.MILLISECONDS, LinkedBlockingDeque())
    private fun run() {
        execute.execute {
            while (player.isPlaying) {
                if(!seekBarTouch){
                    runOnUiThread {
                        position=player.currentPosition
                        mc_videoController.bar.progress = player.currentPosition
                    }
                }
                Shortcut.sleep(400)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FINISH) {
            finish()
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    inner class SurfaceViewCallback:SurfaceHolder.Callback{
        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            videoPause()
            player.setDisplay(null)
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            player.setDisplay(holder)
            player.seekTo(player.currentPosition)
            toggleShow()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            WindowUtil.setFullScreen(window,true)
        }else{
            WindowUtil.setFullScreen(window,false)
        }
        sizeChanged()
    }

    override fun onBackPressed() {
        if(resources.configuration.orientation==ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            finish()
        }else{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }




    companion object {
        private const val SONG_ID="songId"
        @JvmStatic
        fun actionStart(ctx: Context, videoId: String="",songId:String="") {
            val intent = Intent(ctx, VideoEntryActivity::class.java)
            intent.putExtra(INTENT_DATA, videoId)
            intent.putExtra(SONG_ID, songId)
            ctx.startActivity(intent)
        }

    }
}