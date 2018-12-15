package com.web.moudle.entry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.View
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.data.InternetMusicDetail
import com.web.moudle.entry.bean.MusicDetailInfo
import com.web.moudle.entry.model.DetailMusicViewModel
import com.web.moudle.musicDownload.service.FileDownloadService
import com.web.web.R
import kotlinx.android.synthetic.main.activity_music_detail.*
import java.lang.StringBuilder

class MusicDetailActivity : BaseActivity() {
    private lateinit var id: String
    private lateinit var model: DetailMusicViewModel
    override fun getLayoutId(): Int {
        return R.layout.activity_music_detail
    }

    private fun loadData() {
        id = if (intent.getStringExtra(ID) == null) "2065932" else intent.getStringExtra(ID)
        model = ViewModelProviders.of(this)[DetailMusicViewModel::class.java]
        model.detailMusic.observe(this, Observer<LiveDataWrapper<MusicDetailInfo>> { data ->
            if (data != null) {
                if (data.code == DetailMusicViewModel.CODE_OK) {
                    val res = data.value
                    tv_musicName.text = res.songInfo.title
                    tv_mainSinger.text = res.songInfo.artistName
                    tv_duration.text = ResUtil.timeFormat("mm:ss", res.songInfo.duration.toLong() * 1000)
                    tv_album.text = res.songInfo.albumName
                    tv_publishTime.text = res.songInfo.publishTime
                    tv_publishCompany.text = res.songInfo.proxyCompany
                    tv_listenTimes.text = res.songInfo.listenTimes
                    tv_downloadMusic.text = ResUtil.getFileSize(res.bitRate.fileSize)
                    val drawable = getDrawable(R.drawable.download)
                    drawable!!.setBounds(0, 0, ViewUtil.dpToPx(20f), ViewUtil.dpToPx(20f))
                    tv_downloadMusic.setCompoundDrawables(drawable, null, null, null)
                    tv_downloadMusic.setOnClickListener {
                        FileDownloadService.addTask(it.context, attributesMap(res))
                    }
                    ImageLoad.loadAsBitmap(res.songInfo.artistPic500x500).into(object : BaseGlideTarget() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Palette.from(resource).generate {
                                it?.vibrantSwatch?.let { sw ->
                                    collapseToolbarLayout.setBackgroundColor(sw.rgb)
                                }
                            }
                            iv_bigImage_detailMusicActivity.setImageBitmap(resource)
                        }
                    })
                    rootView.showContent()
                    model.getLyrics(res.songInfo.lrcLink)
                } else if (data.code == DetailMusicViewModel.CODE_ERROR) {
                    rootView.showError()
                    rootView.errorClickLinsten = View.OnClickListener {
                        model.getDetail(songId = id)
                        rootView.showLoading()
                    }
                }

            }

        })
        model.lyrics.observe(this, Observer { wrapper ->
            if (wrapper == null) return@Observer
            if (wrapper.code == DetailMusicViewModel.CODE_OK) {
                val builder=StringBuilder()
                wrapper.value.forEach {
                    builder.append(it.line)
                    builder.append("\n")
                }
                lyricsView.text=builder.toString()
            }
        })
        model.getDetail(id)
    }

    override fun initView() {
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)
        loadData()
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, dy ->
            val offset = -dy
            when (offset) {
                appBar.totalScrollRange -> {//**完全折叠
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    toolbar.setBackgroundColor(getColor(R.color.themeColor))
                }
                else -> {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        })
    }

    private fun attributesMap(info: MusicDetailInfo): InternetMusicDetail {
        val res = info.songInfo
        return InternetMusicDetail(
                songId = res.songId,
                songName = res.title,
                artistName = res.artistName,
                duration = res.duration.toInt(),
                size = info.bitRate.fileSize,
                lrcLink = res.lrcLink,
                songLink = info.bitRate.songLink,
                singerIconSmall = res.picSmall,
                albumId = res.albumId,
                albumName = res.albumName,
                format = info.bitRate.format
        )
    }


    companion object {
        private const val ID = "id"
        @JvmStatic
        fun actionStart(ctx: Context, id: String) {
            val intent = Intent(ctx, MusicDetailActivity::class.java)
            intent.putExtra(ID, id)
            ctx.startActivity(intent)
        }
    }

}