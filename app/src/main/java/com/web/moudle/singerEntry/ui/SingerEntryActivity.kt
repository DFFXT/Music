package com.web.moudle.singerEntry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.WindowUtil
import com.web.data.InternetMusicDetail
import com.web.misc.GapItemDecoration
import com.web.moudle.musicEntry.bean.MusicDetailInfo
import com.web.moudle.singerEntry.adapter.SingerAlbumAdapter
import com.web.moudle.singerEntry.adapter.SingerSongAdapter
import com.web.moudle.singerEntry.bean.AlbumEntryBox
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.bean.SongEntryBox
import com.web.moudle.singerEntry.model.SingerEntryViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_singer_entry.*

class SingerEntryActivity : BaseActivity() {
    private lateinit var id: String
    private lateinit var model: SingerEntryViewModel

    private val limit=9

    private lateinit var rootView:ViewGroup
    override fun getLayoutId(): Int {
        return R.layout.activity_singer_entry
    }

    private fun loadData() {
        id = if (intent.getStringExtra(ID) == null) "2065932" else intent.getStringExtra(ID)
        model = ViewModelProviders.of(this)[SingerEntryViewModel::class.java]
        //**歌手信息观测
        model.singerInfo.observe(this, Observer<LiveDataWrapper<SingerInfo>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
                    //**请求音乐列表
                    model.getSongList(id,0,limit)
                    model.getAlbumList(id,0,limit)
                    val res = data.value
                    tv_singerName.text = res.name
                    tv_country.text = res.country
                    tv_birth.text = res.birth
                    tv_constellation.text = res.constellation
                    tv_company.text = res.company
                    tv_totalSongs.text = res.totalSongs
                    tv_totalAlbums.text = res.albumTotal
                    tv_totalMV.text = "${res.totalMv}"

                    ex_introduction.text=res.introduction
                    //**加载图片
                    ImageLoad.loadAsBitmap(res.avatar500).into(object : BaseGlideTarget() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            Palette.from(resource).generate {
                                it?.vibrantSwatch?.let { sw ->
                                    findViewById<View>(R.id.collapseToolbarLayout).setBackgroundColor(sw.rgb)
                                }
                            }
                            findViewById<ImageView>(R.id.iv_bigImage_detailMusicActivity).setImageBitmap(resource)
                        }
                    })
                    rv_singerEntry.showLoading()
                    rootView.showContent()
                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
                    rootView.showError()
                    rootView.errorClickLinsten = View.OnClickListener {
                        model.getArtistInfo(id)
                        rootView.showLoading()
                    }
                }

            }

        })

        //**音乐列表观测
        model.songList.observe(this,Observer<LiveDataWrapper<SongEntryBox>>{wrapper->
            if(wrapper==null)return@Observer
            if(wrapper.code==LiveDataWrapper.CODE_OK){
                rv_singerEntry.showContent()
                rv_singerEntry.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                rv_singerEntry.addItemDecoration(GapItemDecoration(right = 20,remainEndPadding = true))
                val adapter=SingerSongAdapter(this@SingerEntryActivity,wrapper.value.songList)
                rv_singerEntry.adapter=adapter
                if(wrapper.value.haveMore==0){
                    tv_moreMusic.visibility=View.GONE
                }
            }else if(wrapper.code==LiveDataWrapper.CODE_ERROR){
                rv_singerEntry.showError()
            }
        })

        //**专辑列表观测
        model.albumList.observe(this,Observer<LiveDataWrapper<AlbumEntryBox>>{wrapper->
            if(wrapper==null)return@Observer
            if(wrapper.code==LiveDataWrapper.CODE_OK){
                rv_albumEntry.showContent()
                rv_albumEntry.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                rv_albumEntry.addItemDecoration(GapItemDecoration(right = 20,remainEndPadding = true))
                val adapter= SingerAlbumAdapter(this@SingerEntryActivity,wrapper.value.albumList)
                rv_albumEntry.adapter=adapter
                if(wrapper.value.haveMore==0){
                    tv_moreAlbum.visibility=View.GONE
                }
            }else if(wrapper.code==LiveDataWrapper.CODE_ERROR){
                rv_albumEntry.showError()
            }
        })

        model.getArtistInfo(id)
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
            val intent = Intent(ctx, SingerEntryActivity::class.java)
            intent.putExtra(ID, id)
            ctx.startActivity(intent)
        }
    }

}