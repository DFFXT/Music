package com.web.moudle.albumEntry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.data.InternetMusicDetail
import com.web.misc.DrawableItemDecoration
import com.web.misc.ExpandableTextView
import com.web.misc.GapItemDecoration
import com.web.moudle.albumEntry.adapter.AlbumListAdapter
import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.albumEntry.bean.OtherSong
import com.web.moudle.albumEntry.model.AlbumEntryViewModel
import com.web.moudle.musicEntry.model.DetailMusicViewModel
import com.web.moudle.musicDownload.service.FileDownloadService
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.web.R
import kotlinx.android.synthetic.main.activity_album_entry.*
import kotlinx.android.synthetic.main.activity_music_detail.*
import java.lang.StringBuilder

class AlbumEntryActivity : BaseActivity() {
    private lateinit var id: String
    private lateinit var model: AlbumEntryViewModel
    private lateinit var rootView:ViewGroup
    override fun getLayoutId(): Int {
        return R.layout.activity_album_entry
    }

    private fun loadData() {
        id = intent.getStringExtra(ID)
        model = ViewModelProviders.of(this)[AlbumEntryViewModel::class.java]
        model.albumResponse.observe(this, Observer<LiveDataWrapper<AlbumResponse>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
                    val res = data.value
                    findViewById<TextView>(R.id.tv_musicName).text = res.albumInfo.albumName
                    findViewById<TextView>(R.id.tv_mainSinger).text = res.albumInfo.artistName
                    tv_styles.text=res.albumInfo.styles
                    findViewById<TextView>(R.id.tv_publishTime).text = res.albumInfo.publishTime
                    findViewById<TextView>(R.id.tv_publishCompany).text = res.albumInfo.publishCompany
                    findViewById<TextView>(R.id.tv_listenTimes).text =res.albumInfo.listenNum
                    findViewById<ExpandableTextView>(R.id.ex_introduction).text=res.albumInfo.info
                    ImageLoad.loadAsBitmap(res.albumInfo.pic500).into(object : BaseGlideTarget() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Palette.from(resource).generate {
                                it?.vibrantSwatch?.let { sw ->
                                    findViewById<CollapsingToolbarLayout>(R.id.collapseToolbarLayout).setBackgroundColor(sw.rgb)
                                }
                            }
                            findViewById<ImageView>(R.id.iv_bigImage_detailMusicActivity).setImageBitmap(resource)
                        }
                    })
                    val adapter=AlbumListAdapter(this@AlbumEntryActivity,res.otherSong)
                    rv_albumList.adapter=adapter
                    rootView.showContent()
                    //model.getLyrics(res.songInfo.lrcLink)
                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
                    rootView.showError()
                    rootView.errorClickLinsten = View.OnClickListener {
                        model.getAlbumInfo(id)
                        rootView.showLoading()
                    }
                }

            }

        })
        model.lyrics.observe(this, Observer { wrapper ->
            if (wrapper == null) return@Observer
            if (wrapper.code == LiveDataWrapper.CODE_OK) {
                val builder=StringBuilder()
                wrapper.value.forEach {
                    builder.append(it.line)
                    builder.append("\n")
                }
                lyricsView.text=builder.toString()
            }
        })
        model.getAlbumInfo(id)
    }

    override fun initView() {
        rootView=findViewById(R.id.rootView)
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)
        loadData()
        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        findViewById<AppBarLayout>(R.id.appBarLayout).addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, dy ->
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
        rv_albumList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_albumList.addItemDecoration(DrawableItemDecoration(orientation = LinearLayoutManager.VERTICAL,bottom = 20,drawable = getDrawable(R.drawable.dash_line_1px)!!))
    }

    /*private fun attributesMap(info: MusicDetailInfo): InternetMusicDetail {
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
    }*/


    companion object {
        private const val ID = "id"
        @JvmStatic
        fun actionStart(ctx: Context, id: String) {
            val intent = Intent(ctx, AlbumEntryActivity::class.java)
            intent.putExtra(ID, id)
            ctx.startActivity(intent)
        }
    }

}