package com.web.moudle.musicSearch.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.music.m.R
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.web.common.base.setItemDecoration
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.musicSearch.adapter.SimpleArtistAdapter
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo
import com.web.moudle.musicSearch.viewModel.ArtistViewModel
import com.web.moudle.singerEntry.ui.SingerEntryActivity

class ArtistFragment : BaseSearchFragment() {
    private lateinit var vm: ArtistViewModel
    private lateinit var adapter: SimpleArtistAdapter

    override fun getLayoutId(): Int {
        return R.layout.fragment_music_search
    }

    override fun initView(rootView: View) {
        vm = ViewModelProviders.of(this)[ArtistViewModel::class.java]

        vm.artistId.observe(this, Observer<String> {
            if (it != null) {
                SingerEntryActivity.actionStart(requireContext(), it)
            } else {
                MToast.showToast(requireContext(), getString(R.string.dataAnalyzeError))
            }
        })
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        binding.smartRefreshLayout.setEnableRefresh(false)
        binding.smartRefreshLayout.setEnableOverScrollDrag(true)
        binding.smartRefreshLayout.setEnableLoadMore(true)
        binding.smartRefreshLayout.setRefreshFooter(ClassicsFooter(context))

        binding.rvMusicList.layoutManager = LinearLayoutManager(context)

        binding.rvMusicList.setItemDecoration(DrawableItemDecoration(
            bottom = 20,
            left = 20,
            right = 20,
            drawable = ResUtil.getDrawable(R.drawable.dash_line_1px),
            orientation = LinearLayoutManager.VERTICAL
        ))
        adapter = SimpleArtistAdapter()
        adapter.itemClick = {
            if (it != null) {
                // 无法通过这个 artistId 获取正确信息，必须通过一次跳转获取真正的 artistId
                // http://music.taihe.com/data/artist/redirect?id=14413780
                vm.getRedirectHeader(it.artistId)
                // SingerEntryActivity.actionStart(context!!, it.artistId)
            }
        }
        binding.rvMusicList.adapter = adapter

        vm.status.observe(this, Observer<LiveDataWrapper<Int>> {
            if (it!!.code == LiveDataWrapper.CODE_NO_DATA) {
                binding.smartRefreshLayout.setNoMoreData(true)
            } else if (it.code == LiveDataWrapper.CODE_OK) {
                searchCallBack?.invoke(it.value)
                if (it.value == 0) {
                    binding.smartRefreshLayout.showError("没有数据", ColorDrawable())
                        .setBackgroundColor(Color.TRANSPARENT)
                } else {
                    binding.smartRefreshLayout.showContent()
                }
            }
        })

        search(keyword)
    }

    override fun search(keyword: String?) {
        this.keyword = keyword
        if (!isInit()) return
        rootView?.showLoading()
        binding.smartRefreshLayout.setNoMoreData(false)
        vm.search(keyword).observe(this, Observer<PagedList<SimpleArtistInfo>> {
            adapter.submitList(it)
            rootView?.showContent()
        })
    }
}
