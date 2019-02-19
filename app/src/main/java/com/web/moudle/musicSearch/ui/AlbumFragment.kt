package com.web.moudle.musicSearch.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.setItemDecoration
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicSearch.adapter.SimpleAlbumAdapter
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.viewModel.AlbumViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_music_search.*

class AlbumFragment:BaseSearchFragment() {
    private lateinit var vm: AlbumViewModel
    private lateinit var adapter:SimpleAlbumAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_music_search
    }

    override fun initView(rootView: View) {
        vm= ViewModelProviders.of(this)[AlbumViewModel::class.java]
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        smartRefreshLayout.setEnableRefresh(false)
        smartRefreshLayout.setEnableOverScrollDrag(true)
        smartRefreshLayout.setEnableLoadMore(true)
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(context))

        rv_musicList.layoutManager= LinearLayoutManager(context)
        rv_musicList.setItemDecoration(DrawableItemDecoration(bottom = 20,left = 20,right = 20,
                drawable = ResUtil.getDrawable(R.drawable.dash_line_1px),orientation = LinearLayoutManager.VERTICAL))
        adapter= SimpleAlbumAdapter()
        adapter.itemClick={
            if(it!=null){
                AlbumEntryActivity.actionStart(context!!,it.albumId)
            }
        }
        rv_musicList.adapter=adapter

        vm.status.observe(this,Observer<LiveDataWrapper<Int>>{
            if(it!!.code== LiveDataWrapper.CODE_NO_DATA){
                smartRefreshLayout.setNoMoreData(true)
            }else if(it.code==LiveDataWrapper.CODE_OK){
                searchCallBack?.invoke(it.value)
                if(it.value==0){
                    smartRefreshLayout.showError("没有数据",ColorDrawable())
                            .setBackgroundColor(Color.TRANSPARENT)
                }else{
                    smartRefreshLayout.showContent()
                }
            }
        })

        search(keyword)
    }
    override fun search(keyword:String?){
        this.keyword=keyword
        if(!isInit())return
        rootView?.showLoading()
        smartRefreshLayout.setNoMoreData(false)
        vm.search(keyword).observe(this,Observer<PagedList<SimpleAlbumInfo>>{
            adapter.submitList(it)
            rootView?.showContent()
        })
    }

}