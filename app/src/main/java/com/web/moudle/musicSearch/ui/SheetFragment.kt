package com.web.moudle.musicSearch.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.setItemDecoration
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.misc.DrawableItemDecoration
import com.web.misc.GapItemDecoration
import com.web.moudle.musicSearch.adapter.SimpleSheetAdapter
import com.web.moudle.musicSearch.bean.SimpleSongSheet
import com.web.moudle.musicSearch.model.SheetViewModel
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.web.moudle.songSheetEntry.ui.SongSheetActivity
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_music_search.*

class SheetFragment:BaseSearchFragment() {
    private lateinit var vm: SheetViewModel
    private lateinit var adapter: SimpleSheetAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_music_search
    }

    override fun initView(rootView: View) {
        vm= ViewModelProviders.of(this)[SheetViewModel::class.java]

        vm.artistId.observe(this, Observer<String> {
            if(it!=null){
                SingerEntryActivity.actionStart(context!!,it)
            }else{
                MToast.showToast(context!!,getString(R.string.dataAnalyzeError))
            }

        })
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        smartRefreshLayout.setEnableRefresh(false)
        smartRefreshLayout.setEnableOverScrollDrag(true)
        smartRefreshLayout.setEnableLoadMore(true)
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(context))

        rv_musicList.layoutManager= LinearLayoutManager(context)

        rv_musicList.setItemDecoration(DrawableItemDecoration(bottom = 20,left = 20,right = 20,
                drawable = ResUtil.getDrawable(R.drawable.dash_line_1px),orientation = LinearLayoutManager.VERTICAL))
        adapter= SimpleSheetAdapter()
        adapter.itemClick={
            if(it!=null){//**进入歌单
                SongSheetActivity.actionStart(context!!,it.sheetId)
            }
        }
        rv_musicList.adapter=adapter

        vm.status.observe(this,Observer<LiveDataWrapper<Throwable>>{
            if(it!!.code==LiveDataWrapper.CODE_NO_DATA){
                smartRefreshLayout.setNoMoreData(true)
            }
        })

        search(keyword)
    }
    override fun search(keyword:String?){
        this.keyword=keyword
        if(!isInit())return
        rootView?.showLoading()
        smartRefreshLayout.setNoMoreData(false)
        vm.search(keyword).observe(this,Observer<PagedList<SimpleSongSheet>>{
            adapter.submitList(it)
            rootView?.showContent()
        })
    }
}