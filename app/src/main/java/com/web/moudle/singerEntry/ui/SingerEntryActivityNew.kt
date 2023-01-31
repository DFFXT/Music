package com.web.moudle.singerEntry.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.WindowUtil
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.model.SingerEntryViewModel
import com.web.moudle.singerEntry.ui.fragment.SingerAllAlbumFragment
import com.web.moudle.singerEntry.ui.fragment.SingerAllMusicFragment
import com.music.m.R
import kotlinx.android.synthetic.main.activity_singer_entry_new.*

class SingerEntryActivityNew : BaseActivity() {
    private lateinit var id: String
    private lateinit var model: SingerEntryViewModel

    private var title=""
    private val pageList=ArrayList<BaseFragment>()



    override fun getLayoutId(): Int {
        return R.layout.activity_singer_entry_new
    }

    private fun loadData() {
        id = if (intent.getStringExtra(ID) == null) "2065932" else intent.getStringExtra(ID)!!

        pageList.add(SingerAllMusicFragment.getInstance(id))
        pageList.add(SingerAllAlbumFragment.getInstance(id))

        model = ViewModelProviders.of(this)[SingerEntryViewModel::class.java]
        //**歌手信息观测
        model.singerInfo.observe(this, Observer<LiveDataWrapper<SingerInfo>> { data ->
            if (data != null) {
                if (data.code == LiveDataWrapper.CODE_OK) {
                    val res = data.value
                    title=res.name
                    tv_artistName.text=res.name
                    tv_desc.text=res.introduction
                    ImageLoad.load(res.avatar500).into(iv_bigImage_detailMusicActivity)
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

        viewPager.adapter=BaseFragmentPagerAdapter(supportFragmentManager,pageList)

        tabLayout.setupWithViewPager(viewPager)
        pageList.forEachIndexed {index,fragment->
            tabLayout.getTabAt(index)?.text = fragment.title
        }




        model.getArtistInfo(id)

    }

    override fun initView() {
        rootView.showLoading(true)
        WindowUtil.setImmersedStatusBar(window)
        toolbar.setPadding(topBar.paddingStart,WindowUtil.getStatusHeight(),topBar.paddingEnd,topBar.paddingBottom)
        loadData()
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, dy ->
            when (-dy) {
                appBar.totalScrollRange -> {
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    toolbar.setBackgroundColor(getColor(R.color.themeColor))
                    topBar.post {
                        topBar.setMainTitle(title)
                    }

                }
                else -> {
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    topBar.post {
                        topBar.setMainTitle("")
                    }
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