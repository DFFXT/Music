package com.web.moudle.albumEntry.ui

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.music.m.R
import com.music.m.databinding.ActivityAlbumEntryBinding
import com.web.common.base.BaseActivity2
import com.web.common.base.bitmapColorSet
import com.web.common.base.errorClickLinsten
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.WindowUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.albumEntry.adapter.AlbumListAdapter
import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.albumEntry.model.AlbumEntryViewModel

class AlbumEntryActivity : BaseActivity2<ActivityAlbumEntryBinding>() {
    private lateinit var id: String
    private lateinit var model: AlbumEntryViewModel
    override fun getLayoutId(): Int {
        return R.layout.activity_album_entry
    }

    private fun loadData() {
        id = intent.getStringExtra(ID)!!
        model = ViewModelProviders.of(this)[AlbumEntryViewModel::class.java]
        model.albumResponse.observe(this, Observer<LiveDataWrapper<AlbumResponse>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
                    val res = data.value
                    binding.tvMusicName.text = res.albumInfo.albumName
                    binding.tvMainSinger.text = res.albumInfo.artistName
                    binding.tvStyles.text=res.albumInfo.styles
                    binding.tvPublishTime.text = res.albumInfo.publishTime
                    binding.tvPublishCompany.text = res.albumInfo.publishCompany
                    binding.tvListenTimes.text =res.albumInfo.listenNum
                    binding.exIntroduction.text=res.albumInfo.info
                    bitmapColorSet(res.albumInfo.pic500,binding.ivBigImageDetailMusicActivity,binding.collapseToolbarLayout)
                    binding.rvAlbumList.adapter=AlbumListAdapter(this@AlbumEntryActivity,res.otherSong)
                    binding.rootView.showContent()
                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
                    binding.rootView.showError()
                    binding.rootView.errorClickLinsten = View.OnClickListener {
                        model.getAlbumInfo(id)
                        binding.rootView.showLoading()
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
                binding.exIntroduction.text=builder.toString()
            }
        })
        model.getAlbumInfo(id)
    }

    override fun initView() {
        binding.rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)

        val manager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvAlbumList.layoutManager=manager
        binding.rvAlbumList.addItemDecoration(DrawableItemDecoration(orientation = RecyclerView.VERTICAL,bottom = 2,drawable = getDrawable(R.drawable.recycler_divider)!!))
        loadData()
    }



    companion object {
        private const val ID = "itemId"
        @JvmStatic
        fun actionStart(ctx: Context, id: String) {
            val intent = Intent(ctx, AlbumEntryActivity::class.java)
            intent.putExtra(ID, id)
            ctx.startActivity(intent)
        }
    }

}