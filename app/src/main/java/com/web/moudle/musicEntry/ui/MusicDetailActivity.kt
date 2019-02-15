package com.web.moudle.musicEntry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.Color
import android.os.IBinder
import android.support.design.widget.AppBarLayout
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.view.View
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicForPlay
import com.web.data.Music
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicDownload.service.FileDownloadService
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.moudle.musicEntry.model.DetailMusicViewModel

import com.web.web.R
import kotlinx.android.synthetic.main.activity_music_detail.*

class MusicDetailActivity : BaseActivity() {
    private lateinit var id: String
    private lateinit var model: DetailMusicViewModel
    private var connection: MusicPlay.Connect? = null
    private var serviceConnection: ServiceConnection? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_music_detail
    }

    private fun loadData() {
        id = if (intent.getStringExtra(ID) == null) "2065932" else intent.getStringExtra(ID)
        model = ViewModelProviders.of(this)[DetailMusicViewModel::class.java]
        model.detailMusic.observe(this, Observer<LiveDataWrapper<MusicDetailInfo>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
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
                    //**加载图片
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
                    //**获取歌词
                    model.getLyrics(res.songInfo.lrcLink)
                    val music = InternetMusicForPlay()
                    music.imgAddress = res.songInfo.picSmall
                    music.lrcLink = res.songInfo.lrcLink
                    music.musicName = res.songInfo.title
                    music.singer = res.songInfo.artistName
                    music.path = res.bitRate.songLink


                    var theSameMusic = false
                    //**播放器观测者
                    val observer = object : PlayerObserver() {
                        override fun play() {
                            if (theSameMusic)
                                iv_playIconSwitch.setImageResource(R.drawable.icon_play_white)
                        }

                        override fun pause() {
                            iv_playIconSwitch.setImageResource(R.drawable.icon_pause_white)
                        }

                        override fun load(groupIndex: Int, childIndex: Int, m: Music?, maxTime: Int) {
                            if (m!=null&&musicEqual(music,m)) {
                                theSameMusic = true
                                play()
                            } else {
                                theSameMusic = false
                                pause()
                            }
                        }
                    }

                    val intent = Intent(this@MusicDetailActivity, MusicPlay::class.java)
                    intent.action = MusicPlay.BIND
                    serviceConnection = object : ServiceConnection {
                        override fun onServiceDisconnected(name: ComponentName?) {
                        }

                        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                            connection = service as MusicPlay.Connect
                            connection!!.addObserver(this@MusicDetailActivity, observer)
                            connection!!.getPlayerInfo()
                        }
                    }
                    //**连接 播放器
                    bindService(intent, serviceConnection!!, BIND_AUTO_CREATE)

                    iv_playIconSwitch.setOnClickListener {
                        connection?.let { con ->
                            if (con.config.music == music) {
                                con.changePlayerPlayingStatus()
                            } else {
                                con.playInternet(music)
                            }
                        }
                    }


                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
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
            if (wrapper.code == LiveDataWrapper.CODE_OK) {
                val builder = StringBuilder()
                wrapper.value.forEach {
                    builder.append(it.line)
                    builder.append("\n")
                }
                lyricsView.text = builder.toString()
            }
        })
        model.getDetail(id)
    }

    override fun initView() {
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)
        loadData()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
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

    /**
     * 比较音乐是否是同一个地址
     * http://zhangmenshiting.qianqian.com/data2/music/0cc6430b863f1fc6032a29d42218ddcd/598649096/598649096.m4a?xcode=ec27389241337245e9c5304b21fec782
     * 没次xcode
     **/
    private fun musicEqual(m1: Music, m2: Music):Boolean {
        if(m1.path==m2.path)return true
        val index1=m1.path.indexOf("?")
        val index2=m2.path.indexOf("?")
        if(index1<0||index2<0)return false
        if(m1.path.substring(0,index1)==m2.path.substring(0,index2))return true
        return false

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

    override fun onDestroy() {
        super.onDestroy()
        if (serviceConnection != null) {
            unbindService(serviceConnection!!)
        }

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