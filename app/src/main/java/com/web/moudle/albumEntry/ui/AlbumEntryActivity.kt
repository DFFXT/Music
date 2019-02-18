package com.web.moudle.albumEntry.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.WindowUtil
import com.web.misc.DrawableItemDecoration
import com.web.misc.ExpandableTextView
import com.web.moudle.albumEntry.adapter.AlbumListAdapter
import com.web.moudle.albumEntry.bean.AlbumResponse
import com.web.moudle.albumEntry.model.AlbumEntryViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_album_entry.*

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
                    bitmapColorSet(res.albumInfo.pic500,findViewById(R.id.iv_bigImage_detailMusicActivity),findViewById(R.id.collapseToolbarLayout))
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
        rootView=findViewById(R.id.rootView)
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)

        val manager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_albumList.layoutManager=manager
        rv_albumList.addItemDecoration(DrawableItemDecoration(orientation = LinearLayoutManager.VERTICAL,bottom = 20,drawable = getDrawable(R.drawable.dash_line_1px)!!))
        loadData()
    }



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