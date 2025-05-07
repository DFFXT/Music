package com.web.moudle.billboradDetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.*
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.billboradDetail.adapter.NetMusicListPagedAdapter
import com.web.moudle.billboradDetail.bean.BillBoardInfo
import com.web.moudle.billboradDetail.model.NetMusicListViewModel
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.music.m.R
import com.music.m.databinding.ActivityNetMusicListBinding

class NetMusicListActivity : BaseViewBindingActivity<ActivityNetMusicListBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_net_music_list
    private lateinit var title: String

    private lateinit var model: NetMusicListViewModel

    private var textColor: Int = Color.WHITE

    private var loaded = false

    @SuppressLint("SetTextI18n")
    override fun initView() {
        WindowUtil.setImmersedStatusBar(window)

        title = intent.getStringExtra(INTENT_DATA)!!
        model = ViewModelProviders.of(this)[NetMusicListViewModel::class.java]
        model.response.observe(this, Observer<LiveDataWrapper<BillBoardInfo>> { res ->
            if (loaded) return@Observer

            if (res == null || res.value == null || res.code == LiveDataWrapper.CODE_ERROR) {
                binding.rootView.showError()
                return@Observer
            }
            loaded = true
            val it = res.value
            binding.tvTitle.text = title
            if (it.update_date?.isStrictEmpty() != false) {
                binding.tvUpdateTime.visibility = View.INVISIBLE
            } else {
                binding.tvUpdateTime.text = ResUtil.getString(R.string.updateTime, it.update_date)
            }

            if (it.billboard_songnum.toInt() != 0) {
                binding.tvMusicCount.text = "共${it.billboard_songnum}首"
            }

            binding.collapseToolbarLayout.setBackgroundColor(Color.parseColor(it.bg_color.replace("0x", "#")))
            textColor = Color.parseColor(it.color.replace("0x", "#"))
            binding.tvTitle.setTextColor(textColor)
            binding.tvUpdateTime.setTextColor(textColor)
            binding.tvMusicCount.setTextColor(textColor)
            binding.topBar.setMainTitle(title)
            binding.topBar.setTint(textColor)
            binding.topBar.setMainTitleColor(textColor)
            binding.rootView.showContent()
        })
        binding.rvNetMusicList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvNetMusicList.addItemDecoration(
            DrawableItemDecoration(
                left = 10,
                top = 10,
                right = 10,
                bottom = 10,
                orientation = LinearLayout.VERTICAL,
                drawable = getDrawable(R.drawable.recycler_divider)
            )
        )

        var liveData: LiveData<PagedList<SimpleMusicInfo>>? = null
        when (intent.getIntExtra(REQUEST_TYPE, -1)) {
            NetMusicType.TYPE_BILLBOARD.ordinal -> {
                liveData = model.requestList(intent.getIntExtra(LIST_TYPE, -1))
            }
            NetMusicType.TYPE_TODAY_RECOMMEND.ordinal -> {
                liveData = model.requestRecommend()
            }
            NetMusicType.TYPE_SINGER_ALL_MUSIC.ordinal -> {
                liveData = model.requestSingerAllMusic(intent.getStringExtra(UID)!!)
            }
            NetMusicType.TYPE_SINGER_ALL_ALBUM.ordinal -> {
                liveData = model.requestSingerAllAlbum(intent.getStringExtra(UID)!!)
            }
        }
        val adapter = NetMusicListPagedAdapter(NetMusicListPagedAdapter.Diff())
        binding.rvNetMusicList.adapter = adapter
        liveData?.observe(this, Observer<PagedList<SimpleMusicInfo>> {
            adapter.submitList(it)
        })

        ViewUtil.setHeight(binding.rvNetMusicList, ViewUtil.screenHeight() - binding.appBarLayout.bottom)

        binding.appBarLayout.addOnOffsetChangedListener(object : BaseAppBarLayoutOffsetChangeListener() {
            override fun offsetChanged(state: Int, position: Int) {
                when (state) {
                    STATE_COLLAPSE -> {
                        binding.topBar.mainTitle.alpha = 1f
                        binding.topBar.startImageView.alpha = 1f
                        binding.topBar.setTint(Color.WHITE)
                        binding.topBar.setMainTitleColor(Color.WHITE)
                    }
                    STATE_EXPANDING -> {
                        var alpha = 1 - (binding.appBarLayout.totalScrollRange - position) / binding.appBarLayout.totalScrollRange.toFloat()
                        if (alpha < 0.5f) alpha = 0f
                        binding.topBar.mainTitle.alpha = alpha * 0.5f
                        binding.topBar.startImageView.alpha = alpha * 0.5f
                        binding.topBar.setTint(Color.WHITE)
                        binding.topBar.setMainTitleColor(Color.WHITE)
                    }
                    STATE_EXPANDED -> {
                        binding.topBar.setTint(textColor)
                        binding.topBar.setMainTitleColor(textColor)
                    }
                }
            }
        })
        binding.smartRefreshLayout.setEnableOverScrollDrag(true)
        binding.rootView.showLoading(true)
    }

    companion object {
        const val LIST_TYPE = "type"
        const val REQUEST_TYPE = "req_type"
        const val UID = "uid"

        /**
         * 排行榜
         */
        @JvmStatic
        fun actionStartBillboard(ctx: Context, title: String, type: Int) {
            val intent = Intent(ctx, NetMusicListActivity::class.java)
            intent.putExtra(INTENT_DATA, title)
            intent.putExtra(REQUEST_TYPE, NetMusicType.TYPE_BILLBOARD.ordinal)
            intent.putExtra(LIST_TYPE, type)
            ctx.startActivity(intent)
        }

        /**
         * 今日推荐
         */
        @JvmStatic
        fun actionStartRecommend(ctx: Context, title: String) {
            val intent = Intent(ctx, NetMusicListActivity::class.java)
            intent.putExtra(INTENT_DATA, title)
            intent.putExtra(REQUEST_TYPE, NetMusicType.TYPE_TODAY_RECOMMEND.ordinal)
            ctx.startActivity(intent)
        }

        /**
         * 歌手所有歌曲
         */
        @JvmStatic
        fun actionStartSingerMusic(ctx: Context, title: String, uid: String) {
            val intent = Intent(ctx, NetMusicListActivity::class.java)
            intent.putExtra(INTENT_DATA, title)
            intent.putExtra(REQUEST_TYPE, NetMusicType.TYPE_SINGER_ALL_MUSIC.ordinal)
            intent.putExtra(UID, uid)
            ctx.startActivity(intent)
        }

        /**
         * 歌手所有专辑
         */
        @JvmStatic
        fun actionStartSingerAlbum(ctx: Context, title: String, uid: String) {
            val intent = Intent(ctx, NetMusicListActivity::class.java)
            intent.putExtra(INTENT_DATA, title)
            intent.putExtra(REQUEST_TYPE, NetMusicType.TYPE_SINGER_ALL_ALBUM.ordinal)
            intent.putExtra(UID, uid)
            ctx.startActivity(intent)
        }
    }
}

enum class NetMusicType constructor(type: Int) {
    TYPE_BILLBOARD(1),
    TYPE_TODAY_RECOMMEND(2),
    TYPE_SINGER_ALL_MUSIC(3),
    TYPE_SINGER_ALL_ALBUM(4),
}