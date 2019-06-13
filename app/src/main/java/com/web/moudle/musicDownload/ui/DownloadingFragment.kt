package com.web.moudle.musicDownload.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.tool.Ticker
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.ConfirmDialog
import com.web.misc.DrawableItemDecoration
import com.web.moudle.musicDownload.adpter.DownloadingAdapter
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.web.web.R
import kotlinx.coroutines.Dispatchers


class DownloadingFragment:BaseDownloadFragment(){
    override var title: String=ResUtil.getString(R.string.downloading)
    private val adapter=DownloadingAdapter()
    private var dataList:MutableList<DownloadMusic>?=null
    private var dialog:ConfirmDialog?=null
    private val ticker=Ticker(1000,0,Dispatchers.Main){
        adapter.notifyDataSetChanged()
    }
    override fun getLayoutId(): Int =R.layout.view_recycler

    override fun initView(rootView: View) {
        (rootView as RecyclerView).layoutManager=LinearLayoutManager(context)
        rootView.adapter=adapter
        rootView.addItemDecoration(DrawableItemDecoration(bottom = ViewUtil.dpToPx(2f),
                drawable = ResUtil.getDrawable(R.drawable.dash_line_1px)))

        adapter.click={v,position->
            val status=dataList!![position].status
            val id=dataList!![position].internetMusicDetail.id
            when(v.id){
                R.id.downloadStatu->{
                    if (status == DownloadMusic.DOWNLOAD_DOWNLOADING) {
                        connect?.pause(id)
                    } else {
                        connect?.start(id)
                    }
                }
                R.id.close->{
                    if(dialog==null){
                        dialog=ConfirmDialog(context!!)
                                .setMsg(ResUtil.getString(R.string.delete))
                                .setLeftText(ResUtil.getString(R.string.no))
                                .setRightText(ResUtil.getString(R.string.yes))
                                .setRightListener { dialog ->
                                    connect?.delete(id)
                                    dialog.dismiss()
                                }
                                .setLeftListener { dialog ->
                                    dialog.dismiss()
                                }
                    }
                    dialog?.showCenter(v)
                }
            }
        }
        super.initView(rootView)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden){
            val topBarLayout=(activity as MusicDownLoadActivity).topBarLayout
            topBarLayout.setEndText(null)
            topBarLayout.setEndImageListener(null)
        }
    }

    override fun progressChange(id: Int, progress: Long, max: Long) {
        ticker.start()
    }

    override fun statusChange(id: Int, isDownload: Boolean) {
        adapter.notifyDataSetChanged()
    }

    override fun listChanged(downloadMusicList: MutableList<DownloadMusic>, completeList: MutableList<DownloadMusic>) {
        adapter.update(downloadMusicList)
        dataList=downloadMusicList
    }

    override fun onDestroy() {
        super.onDestroy()
        ticker.stop()
    }
}