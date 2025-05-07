package com.web.moudle.home.video

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseFragment
import com.web.common.util.ViewUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.home.video.adapter.VideoRecommendAdapter
import com.web.moudle.home.video.bean.FeedData
import com.web.moudle.home.video.model.VideoRecommendViewModel
import com.music.m.R
import com.music.m.databinding.FragmentVideoBinding
import com.scwang.smart.refresh.footer.ClassicsFooter

class VideoRecommendFragment : BaseFragment<FragmentVideoBinding>() {

    private var vm: VideoRecommendViewModel? = null

    private val videoList = ArrayList<FeedData>()
    private val adapter = VideoRecommendAdapter()

    override fun getLayoutId(): Int = R.layout.fragment_video

    override fun initView(rootView: View) {
        vm = ViewModelProviders.of(this)[VideoRecommendViewModel::class.java]

        vm!!.recommendVideo.observe(this, Observer {
            binding.srlVideo.finishLoadMore()
            videoList.addAll(it.feed_data)
            adapter.update(videoList)
        })
        binding.srlVideo.setRefreshFooter(ClassicsFooter(context))
        binding.srlVideo.setOnLoadMoreListener {
            vm?.getRecommendVideo()
        }
        binding.rvVideo.layoutManager = LinearLayoutManager(context)
        val gap = ViewUtil.dpToPx(10f)
        binding.rvVideo.addItemDecoration(GapItemDecoration(gap, gap, gap, gap, remainEndPadding = true))
        binding.rvVideo.adapter = adapter
        val fakeList = ArrayList<FeedData>()
        for (i in 0..5) {
            fakeList.add(FeedData())
        }
        adapter.update(fakeList)

        vm!!.getRecommendVideo()
    }
}