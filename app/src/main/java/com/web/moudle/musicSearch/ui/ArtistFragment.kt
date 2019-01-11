package com.web.moudle.musicSearch.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.database.Observable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.tool.MToast
import com.web.misc.GapItemDecoration
import com.web.moudle.musicSearch.adapter.SimpleArtistAdapter
import com.web.moudle.musicSearch.bean.SimpleArtistInfo
import com.web.moudle.musicSearch.model.ArtistViewModel
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_music_search.*

class ArtistFragment:BaseSearchFragment() {
    private lateinit var vm: ArtistViewModel
    private lateinit var adapter:SimpleArtistAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_music_search
    }

    override fun initView(rootView: View) {
        vm= ViewModelProviders.of(this)[ArtistViewModel::class.java]

        vm.artistId.observe(this, Observer<String> {
            if(it!=null){
                SingerEntryActivity.actionStart(context!!,it!!)
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

        rv_musicList.addItemDecoration(GapItemDecoration(bottom = 20,left = 20))
        adapter= SimpleArtistAdapter()
        adapter.itemClick={
            if(it!=null){//**无法提通过这个artistId获取正确信息，必须通过一次跳转获取真正的artistId
                //http://music.taihe.com/data/artist/redirect?id=14413780
                vm.getRedirectHeader(it.artistId)
                //SingerEntryActivity.actionStart(context!!,it.artistId)
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
        vm.search(keyword).observe(this,Observer<PagedList<SimpleArtistInfo>>{
            adapter.submitList(it)
            rootView?.showContent()
        })
    }
}