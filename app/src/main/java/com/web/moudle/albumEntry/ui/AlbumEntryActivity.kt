package com.web.moudle.albumEntry.ui

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.WindowUtil
import com.web.misc.DrawableItemDecoration
import com.web.misc.ExpandableTextView
import com.web.moudle.albumEntry.adapter.AlbumListAdapter
import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.albumEntry.model.AlbumEntryViewModel
import com.music.m.R
import kotlinx.android.synthetic.main.activity_album_entry.*

class AlbumEntryActivity : BaseActivity() {
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
                    tv_musicName.text = res.albumInfo.albumName
                    tv_mainSinger.text = res.albumInfo.artistName
                    tv_styles.text=res.albumInfo.styles
                    tv_publishTime.text = res.albumInfo.publishTime
                    tv_publishCompany.text = res.albumInfo.publishCompany
                    tv_listenTimes.text =res.albumInfo.listenNum
                    ex_introduction.text=res.albumInfo.info
                    bitmapColorSet(res.albumInfo.pic500,iv_bigImage_detailMusicActivity,collapseToolbarLayout)
                    rv_albumList.adapter=AlbumListAdapter(this@AlbumEntryActivity,res.otherSong)
                    rootView.showContent()
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
                ex_introduction.text=builder.toString()
            }
        })
        model.getAlbumInfo(id)
    }

    override fun initView() {
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)

        val manager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv_albumList.layoutManager=manager
        rv_albumList.addItemDecoration(DrawableItemDecoration(orientation = RecyclerView.VERTICAL,bottom = 2,drawable = getDrawable(R.drawable.recycler_divider)!!))
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