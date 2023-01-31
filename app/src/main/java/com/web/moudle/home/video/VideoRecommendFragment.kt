package com.web.moudle.home.video

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.BaseFragment
import com.web.common.util.ViewUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.home.video.adapter.VideoRecommendAdapter
import com.web.moudle.home.video.bean.FeedData
import com.web.moudle.home.video.model.VideoRecommendViewModel
import com.music.m.R
import kotlinx.android.synthetic.main.fragment_video.view.*

class VideoRecommendFragment:BaseFragment() {

    private var vm:VideoRecommendViewModel?=null

    private val videoList=ArrayList<FeedData>()
    private val adapter=VideoRecommendAdapter()

    override fun getLayoutId(): Int = R.layout.fragment_video


    override fun initView(rootView: View) {
        vm=ViewModelProviders.of(this)[VideoRecommendViewModel::class.java]


        vm!!.recommendVideo.observe(this, Observer {
            rootView.srl_video.finishLoadMore()
            videoList.addAll(it.feed_data)
            adapter.update(videoList)
        })
        rootView.srl_video.setRefreshFooter(ClassicsFooter(context))
        rootView.srl_video.setOnLoadMoreListener {
            vm?.getRecommendVideo()
        }
        rootView.rv_video.layoutManager=LinearLayoutManager(context)
        val gap=ViewUtil.dpToPx(10f)
        rootView.rv_video.addItemDecoration(GapItemDecoration(gap,gap,gap,gap,remainEndPadding = true))
        rootView.rv_video.adapter=adapter
        val fakeList=ArrayList<FeedData>()
        for(i in 0..5){
            fakeList.add(FeedData())
        }
        adapter.update(fakeList)




        vm!!.getRecommendVideo()


    }


}