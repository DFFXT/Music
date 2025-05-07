package com.web.moudle.singerEntry.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.music.m.R
import com.music.m.databinding.ActivitySingerEntryBinding
import com.web.common.base.BaseGlideTarget
import com.web.common.base.BaseViewBindingActivity
import com.web.common.base.errorClickLinsten
import com.web.common.base.hideLoading
import com.web.common.base.isStrictEmpty
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.WindowUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.billboradDetail.NetMusicListActivity
import com.web.moudle.singerEntry.adapter.SingerAlbumAdapter
import com.web.moudle.singerEntry.adapter.SingerSongAdapter
import com.web.moudle.singerEntry.bean.AlbumEntryBox
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.bean.SongEntryBox
import com.web.moudle.singerEntry.model.SingerEntryViewModel

class SingerEntryActivity : BaseViewBindingActivity<ActivitySingerEntryBinding>() {
    private lateinit var id: String
    private lateinit var model: SingerEntryViewModel

    private val limit = 9

    override fun getLayoutId(): Int {
        return R.layout.activity_singer_entry
    }

    private fun loadData() {
        id = if (intent.getStringExtra(ID) == null) "2065932" else intent.getStringExtra(ID)!!
        model = ViewModelProviders.of(this)[SingerEntryViewModel::class.java]
        //**歌手信息观测
        model.singerInfo.observe(this, Observer<LiveDataWrapper<SingerInfo>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
                    //**请求音乐列表
                    model.getSongList(id, 0, limit)
                    model.getAlbumList(id, 0, limit)
                    val res = data.value
                    binding.tvSingerName.text = res.name
                    binding.tvCountry.text = res.country
                    binding.tvBirth.text = res.birth
                    binding.tvConstellation.text = res.constellation
                    binding.tvCompany.text = res.company
                    binding.tvTotalSongs.text = res.totalSongs
                    binding.tvTotalAlbums.text = res.albumTotal
                    binding.tvTotalMV.text = "${res.totalMv}"

                    if (res.introduction?.isStrictEmpty()!=false) {
                        binding.tvIntroductionLabel.text = getString(R.string.singer_introduction_empty)
                    } else {
                        binding.exIntroduction.text = res.introduction
                    }

                    //**加载图片
                    ImageLoad.loadAsBitmap(res.avatar500).into(object : BaseGlideTarget() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            androidx.palette.graphics.Palette.from(resource).generate {
                                it?.vibrantSwatch?.let { sw ->
                                    binding.collapseToolbarLayout.setBackgroundColor(sw.rgb)
                                }
                            }
                            binding.ivBigImageDetailMusicActivity.setImageBitmap(resource)
                        }
                    })
                    binding.rvSingerEntry.showLoading()
                    binding.rvAlbumEntry.showLoading()
                    binding.rootView.showContent()
                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
                    binding.rootView.showError()
                    binding.rootView.errorClickLinsten = View.OnClickListener {
                        model.getArtistInfo(id)
                        binding.rootView.showLoading()
                    }
                }

            }

        })

        //**音乐列表观测
        model.songList.observe(this, Observer<LiveDataWrapper<SongEntryBox>> { wrapper ->
            if (wrapper == null) return@Observer
            if (wrapper.code == LiveDataWrapper.CODE_OK) {
                if (wrapper.value.songList == null) {
                    binding.groupMusicReference.visibility = View.GONE
                    binding.rvSingerEntry.hideLoading()
                    return@Observer
                }
                binding.rvSingerEntry.showContent()
                binding.rvSingerEntry.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                binding.rvSingerEntry.addItemDecoration(GapItemDecoration(right = 20, remainEndPadding = true))
                val adapter = SingerSongAdapter(this@SingerEntryActivity, wrapper.value.songList!!)
                binding.rvSingerEntry.adapter = adapter
                val num = wrapper.value.total
                if (wrapper.value.haveMore == 0 || wrapper.value.songList!!.size == num) {
                    //只能使text为空，添加在了group里面，group里面的view不能单独隐藏
                    binding.tvMoreMusic.text = ""
                }else{
                    binding.tvMoreMusic.setOnClickListener {
                        NetMusicListActivity.actionStartSingerMusic(it.context,binding.tvSingerName.text.toString(),id)
                    }
                }
            } else if (wrapper.code == LiveDataWrapper.CODE_ERROR) {
                binding.rvSingerEntry.showError()
            }
        })

        //**专辑列表观测
        model.albumList.observe(this, Observer<LiveDataWrapper<AlbumEntryBox>> { wrapper ->
            if (wrapper == null) return@Observer
            if (wrapper.code == LiveDataWrapper.CODE_OK) {
                if (wrapper.value.albumList == null) {
                    binding.groupAlbumReference.visibility = View.GONE
                    binding.rvAlbumEntry.hideLoading()
                    return@Observer
                }
                binding.rvAlbumEntry.showContent()
                binding.rvAlbumEntry.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                binding.rvAlbumEntry.addItemDecoration(GapItemDecoration(right = 20, remainEndPadding = true))
                val adapter = SingerAlbumAdapter(this@SingerEntryActivity, wrapper.value.albumList!!)
                binding.rvAlbumEntry.adapter = adapter
                val num = if (wrapper.value.num == null) 0 else wrapper.value.num!!.toInt()
                if (wrapper.value.haveMore == 0 || wrapper.value.albumList!!.size == num) {
                    binding.tvMoreAlbum.text = ""
                }else{
                    binding.tvMoreAlbum.setOnClickListener {
                        NetMusicListActivity.actionStartSingerAlbum(it.context,binding.tvSingerName.text.toString(),id)
                    }
                }
            } else if (wrapper.code == LiveDataWrapper.CODE_ERROR) {
                binding.rvAlbumEntry.showError()
            }
        })

        model.getArtistInfo(id)
        binding.tvMoreAlbum.visibility = View.GONE
    }

    override fun initView() {
        binding.rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)
        loadData()
        val toolbar = binding.toolbar
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, dy ->
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

    companion object {
        private const val ID = "itemId"
        @JvmStatic
        fun actionStart(ctx: Context, id: String) {
            val intent = Intent(ctx, SingerEntryActivityNew::class.java)
            intent.putExtra(ID, id)
            ctx.startActivity(intent)
        }
    }
}