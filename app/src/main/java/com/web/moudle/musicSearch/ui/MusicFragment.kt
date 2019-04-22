package com.web.moudle.musicSearch.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.setItemDecoration
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.musicSearch.adapter.InternetMusicAdapter
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.musicSearch.viewModel.InternetViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_music_search.*

class MusicFragment:BaseSearchFragment() {

    private lateinit var vm: InternetViewModel
    private lateinit var adapter:InternetMusicAdapter



    override fun getLayoutId(): Int {
        return R.layout.fragment_music_search
    }

    override fun initView(rootView: View) {
        vm=ViewModelProviders.of(this)[InternetViewModel::class.java]

    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        smartRefreshLayout.setEnableRefresh(false)
        smartRefreshLayout.setEnableOverScrollDrag(true)
        smartRefreshLayout.setEnableLoadMore(true)
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(context))

        rv_musicList.layoutManager= LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rv_musicList.setItemDecoration(DrawableItemDecoration(bottom = 20,left = 20,right = 20,
                drawable = ResUtil.getDrawable(R.drawable.dash_line_1px),orientation = LinearLayoutManager.VERTICAL))
        adapter=InternetMusicAdapter(context!!)
        adapter.listener = object : InternetMusicAdapter.OnItemClickListener {
            override fun itemClick(music: SimpleMusicInfo) {
                MusicDetailActivity.actionStart(context!!,music.songId)
            }
        }
        rv_musicList.adapter=adapter
        init()
        search(keyword)
    }

    private fun init() {
        vm.status.observe(this, Observer<LiveDataWrapper<Int>>{ wrapper ->
            if (wrapper == null) return@Observer
            when (wrapper.code) {
                LiveDataWrapper.CODE_NO_DATA->{
                    smartRefreshLayout.setNoMoreData(true)
                }
                LiveDataWrapper.CODE_OK -> {
                    searchCallBack?.invoke(wrapper.value)
                    if(wrapper.value==0){
                        smartRefreshLayout.showError("没有数据", ColorDrawable())
                                .setBackgroundColor(Color.TRANSPARENT)
                    }else{
                        smartRefreshLayout.showContent()
                    }
                }
            }
        })
    }


    /**
     * 外部调用搜索
     *
     * @param keyword 关键词
     */
    override fun search(keyword: String?) {
        super.keyword = keyword
        if (!isInit())return
        rootView?.showLoading()
        smartRefreshLayout.setNoMoreData(false)
        vm.setKeyWords(keyword)
        vm.musicList.observe(this, Observer<PagedList<SimpleMusicInfo>>{ pl ->
            adapter.submitList(pl)
            rootView?.showContent()
        })
    }
}