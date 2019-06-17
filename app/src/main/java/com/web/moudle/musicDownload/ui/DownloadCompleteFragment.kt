package com.web.moudle.musicDownload.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.data.Music
import com.web.misc.ConfirmDialog
import com.web.misc.DrawableItemDecoration
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.musicDownload.adpter.CompleteAdapter
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.service.FileDownloadService
import com.web.web.R

class DownloadCompleteFragment : BaseDownloadFragment() {

    override var title: String = ResUtil.getString(R.string.downloadComplete)

    private val adapter = CompleteAdapter()
    private var dataList: MutableList<DownloadMusic>? = null
    private var visible = false
    private var dialog: ConfirmDialog? = null
    override fun getLayoutId(): Int = R.layout.view_recycler

    override fun initView(rootView: View) {
        (rootView as RecyclerView).layoutManager = LinearLayoutManager(context)
        rootView.adapter = adapter
        rootView.addItemDecoration(DrawableItemDecoration(bottom = 2, orientation = RecyclerView.VERTICAL,
                drawable = ResUtil.getDrawable(R.drawable.recycler_divider)))
        adapter.click = { v, position ->
            val detail = dataList!![position].internetMusicDetail
            when (v.id) {
                R.id.iv_play -> {
                    val music = Music(detail.songName, detail.artistName, detail.path)
                    if (Music.exist(music)) {
                        MusicPlay.play(context, music)
                    } else {
                        MToast.showToast(context!!, R.string.fileNotFound)
                    }
                }
                R.id.item_parent -> {
                    MusicDetailActivity.actionStart(context!!, detail.songId)
                }
            }
        }
        super.initView(rootView)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        visible = !hidden
        if (visible && dataList?.size ?: 0 > 0) {
            showClearTag()
        }
    }

    override fun listChanged(downloadMusicList: MutableList<DownloadMusic>, completeList: MutableList<DownloadMusic>) {
        adapter.update(completeList)
        dataList = completeList
        if (visible && completeList.size == 0) {
            (context as MusicDownLoadActivity).topBarLayout.setEndText(null)
            (context as MusicDownLoadActivity).topBarLayout.setEndImageListener(null)
        } else if (visible) {
            showClearTag()
        }
    }

    private fun showClearTag() {
        val topBarLayout = (context as MusicDownLoadActivity).topBarLayout
        topBarLayout.setEndText(ResUtil.getString(R.string.clearGroup))
        topBarLayout.setEndImageListener(View.OnClickListener { v ->
            if (dialog == null) {
                dialog = ConfirmDialog(context!!)
                        .setMsg(ResUtil.getString(R.string.clearAllRecord))
                        .setLeftText(ResUtil.getString(R.string.no))
                        .setRightText(ResUtil.getString(R.string.yes))
                        .setRightListener { dialog ->
                            FileDownloadService.clearCompleteRecord(context)
                            dialog.dismiss()
                        }
                        .setLeftListener { dialog ->
                            dialog.dismiss()
                        }
            }
            dialog?.showCenter(v)
        })
    }
}