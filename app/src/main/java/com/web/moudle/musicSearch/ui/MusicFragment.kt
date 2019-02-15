package com.web.moudle.musicSearch.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedList
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.web.common.base.setItemDecoration
import com.web.common.base.showContent
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.config.Shortcut
import com.web.data.InternetMusicDetail
import com.web.data.InternetMusicDetailList
import com.web.data.InternetMusicForPlay
import com.web.misc.DrawableItemDecoration
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicDownload.service.FileDownloadService
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

        rv_musicList.layoutManager=LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
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
        vm.observerMusicDetail().observe(this, Observer<InternetMusicDetailList>{ detailList ->
            if (detailList == null) return@Observer
            downloadConsider(detailList.songList[0])
        })
        vm.status.observe(this, Observer<LiveDataWrapper<Int>>{ wrapper ->
            if (wrapper == null) return@Observer
            when (wrapper.code) {
                LiveDataWrapper.CODE_NO_DATA->{
                    smartRefreshLayout.setNoMoreData(true)
                }
                LiveDataWrapper.CODE_OK -> {
                    searchCallBack?.invoke(wrapper.value)
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

    /**
     * 网络音乐点击
     *
     * @param music p
     */
    @SuppressLint("SetTextI18n")
    private fun downloadConsider(music: InternetMusicDetail) {
        val builder = AlertDialog.Builder(context)
        builder.create()
        builder.setPositiveButton("取消") { _, _ ->

        }
        builder.setNeutralButton("在线试听") { _, _ ->
            Thread {
                val info = InternetMusicForPlay()
                info.musicName = Shortcut.validatePath(music.songName)
                info.singer = Shortcut.validatePath(music.artistName)
                info.path = music.songLink
                info.imgAddress = music.singerIconSmall
                info.lrcLink = music.lrcLink
                MusicPlay.play(context, info)

            }.start()
        }
        builder.setNegativeButton("下载(" + ResUtil.getFileSize(music.size) + ")") { _, _ ->
            //**网络获取的时间以秒为单位、后面需要毫秒(媒体库里面的单位为毫秒)
            music.duration = music.duration * 1000
            FileDownloadService.addTask(context, music)
        }

        builder.setTitle(music.songName)
        val tv_songName = TextView(context)
        tv_songName.setTextColor(this.resources.getColor(R.color.white, context!!.theme))
        builder.setMessage("歌手：" + music.artistName +
                "\n时长：" + ResUtil.timeFormat("mm:ss", (music.duration * 1000).toLong()) +
                "\n大小：" + ResUtil.getFileSize(music.size))
        builder.show()
    }
}