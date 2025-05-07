package com.web.moudle.singerEntry.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.music.m.R
import com.music.m.databinding.ActivitySingerEntryNewBinding
import com.web.common.base.BaseFragment
import com.web.common.base.BaseFragmentPagerAdapter
import com.web.common.base.BaseViewBindingActivity
import com.web.common.base.errorClickLinsten
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.WindowUtil
import com.web.moudle.singerEntry.bean.SingerInfo
import com.web.moudle.singerEntry.model.SingerEntryViewModel
import com.web.moudle.singerEntry.ui.fragment.SingerAllAlbumFragment
import com.web.moudle.singerEntry.ui.fragment.SingerAllMusicFragment

class SingerEntryActivityNew : BaseViewBindingActivity<ActivitySingerEntryNewBinding>() {
    private lateinit var id: String
    private lateinit var model: SingerEntryViewModel

    private var title = ""
    private val pageList = ArrayList<BaseFragment<*>>()

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
                    title = res.name
                    binding.tvArtistName.text = res.name // 替换 tv_artistName 为 binding.tvArtistName
                    binding.tvDesc.text = res.introduction // 替换 tv_desc 为 binding.tvDesc
                    ImageLoad.load(res.avatar500).into(binding.ivBigImageDetailMusicActivity) // 替换 iv_bigImage_detailMusicActivity 为 binding.ivBigImageDetailMusicActivity
                    binding.rootView.showContent()

                } else if (data.code == LiveDataWrapper.CODE_ERROR) {
                    binding.rootView.showError() // 替换 rootView 为 binding.rootView
                    binding.rootView.errorClickLinsten = View.OnClickListener {
                        model.getArtistInfo(id)
                        binding.rootView.showLoading() // 替换 rootView 为 binding.rootView
                    }
                }

            }
        })

        binding.viewPager.adapter = BaseFragmentPagerAdapter(supportFragmentManager, pageList) // 替换 viewPager 为 binding.viewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager) // 替换 tabLayout 和 viewPager 为 binding.tabLayout 和 binding.viewPager
        pageList.forEachIndexed { index, fragment ->
            binding.tabLayout.getTabAt(index)?.text = fragment.title // 替换 tabLayout 为 binding.tabLayout
        }

        model.getArtistInfo(id)
    }

    override fun initView() {
        binding.rootView.showLoading(true) // 替换 rootView 为 binding.rootView
        WindowUtil.setImmersedStatusBar(window)
        binding.toolbar.setPadding(
            binding.topBar.paddingStart,
            WindowUtil.getStatusHeight(),
            binding.topBar.paddingEnd,
            binding.topBar.paddingBottom
        ) // 替换 toolbar 和 topBar 为 binding.toolbar 和 binding.topBar
        loadData()
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, dy ->
            when (-dy) {
                appBar.totalScrollRange -> {
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    binding.toolbar.setBackgroundColor(getColor(R.color.themeColor)) // 替换 toolbar 为 binding.toolbar
                    binding.topBar.post {
                        binding.topBar.setMainTitle(title) // 替换 topBar 为 binding.topBar
                    }

                }
                else -> {
                    binding.toolbar.setBackgroundColor(Color.TRANSPARENT) // 替换 toolbar 为 binding.toolbar
                    appBar.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                    binding.topBar.post {
                        binding.topBar.setMainTitle("") // 替换 topBar 为 binding.topBar
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