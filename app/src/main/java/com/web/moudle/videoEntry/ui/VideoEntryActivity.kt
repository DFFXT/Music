package com.web.moudle.videoEntry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import com.web.common.base.BaseActivity
import com.web.common.base.onSeekTo
import com.web.common.base.showError
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
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
    override fun getLayoutId(): Int = R.layout.activity_video_entry
    private lateinit var model: VideoViewModel

    override fun initView() {
        videoId = intent.getStringExtra(INTENT_DATA)
        model = ViewModelProviders.of(this)[VideoViewModel::class.java]
        model.videoInfo.observe(this, Observer<VideoInfoBox> {
            if (it == null) {
                vv_video.showError()
                return@Observer
            }
        })

        model.videoUrl.observe(this, Observer<String> {
            if (it == null) {
                vv_video.showError()
                return@Observer
            }
            vv_video.setVideoURI(Uri.parse(it))
        })

        initVideoController()
        model.getVideoInfo(videoId)
    }

    private fun initVideoController() {
        ViewUtil.setHeight(vv_video, (ViewUtil.screenWidth() * 9 / 16f).toInt())
        frame_videoStatus.setOnClickListener { toggleShow() }
        vv_video.setOnClickListener { toggleShow() }
        iv_videoStatus.setOnClickListener {
            if (vv_video.isPlaying) {
                videoPause()
            } else {
                videoStart()
                toggleShow()
            }
        }
        mc_videoController.bar.onSeekTo {
            vv_video.seekTo(it)
        }

        vv_video.setOnPreparedListener {
            videoStart()
            toggleShow()
            mc_videoController.bar.max = it.duration
            val r = ResUtil.timeFormat("mm:ss", it.duration.toLong())
            mc_videoController.tv_videoMaxTime.text = r
        }
        vv_video.setOnCompletionListener {

        }
        vv_video.setOnErrorListener { mp, what, extra ->

            true
        }
    }

    private fun videoStart() {
        iv_videoStatus.setImageResource(R.drawable.icon_video_play)
        vv_video.start()
        run()
    }

    private fun videoPause() {
        iv_videoStatus.setImageResource(R.drawable.icon_video_pause)
        vv_video.pause()
    }

    private var preClickTime = 0L
    private fun toggleShow() {
        if (g_controlGroup.visibility != View.VISIBLE) {
            g_controlGroup.visibility = View.VISIBLE
            mc_videoController.postDelayed({
                if (System.currentTimeMillis() - preClickTime >= 3500) {
                    g_controlGroup.visibility = View.GONE
                }
            }, 3500)
        } else {
            g_controlGroup.visibility = View.GONE
        }
        preClickTime = System.currentTimeMillis()

    }


    private val execute = ThreadPoolExecutor(1, 1, 10, TimeUnit.MILLISECONDS, LinkedBlockingDeque())
    private fun run() {
        execute.execute {
            while (vv_video.isPlaying) {
                runOnUiThread {
                    mc_videoController.bar.progress = vv_video.currentPosition
                    mc_videoController.tv_videoCurrentTime.text = ResUtil.timeFormat("mm:ss", vv_video.currentPosition.toLong())
                }
                Shortcut.sleep(1000)
            }
        }
    }


    companion object {
        @JvmStatic
        fun actionStart(ctx: Context, videoId: String) {
            val intent = Intent(ctx, VideoEntryActivity::class.java)
            intent.putExtra(INTENT_DATA, videoId)
            ctx.startActivity(intent)
        }

    }
}