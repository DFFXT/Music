package com.web.moudle.lyrics

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.DrawableItemDecoration
import com.web.misc.imageDraw.MinSizeOnMeasure
import com.web.moudle.music.player.other.PlayerConfig
import com.web.moudle.musicEntry.adapter.CommentAdapter
import com.web.moudle.musicEntry.bean.CommentItem
import com.web.moudle.musicEntry.model.DetailMusicViewModel
import com.music.m.R
import kotlinx.android.synthetic.main.fragment_comment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommentDialog(private val ctx: FragmentActivity) {
    private var dialog: BottomSheetDialog? = null

    private var songId = ""
    private var page: Int = 0
    private var pageSize = 30
    private var model: DetailMusicViewModel? = null
    private val commentList = ArrayList<CommentItem>()
    private val adapter = CommentAdapter(commentList)
    private var rootView: View? = null

    private fun init() {
        if (dialog != null) return
        dialog = BottomSheetDialog(ctx)
        rootView = LayoutInflater.from(ctx).inflate(R.layout.fragment_comment, null, false)
        rootView!!.rv_comment.measureListener = MinSizeOnMeasure(0, (ViewUtil.screenHeight() * 0.7).toInt())
        dialog?.setContentView(rootView!!)

        model = ViewModelProviders.of(ctx)[DetailMusicViewModel::class.java]

        if (songId != PlayerConfig.music?.song_id) {
            page = 0
            commentList.clear()
            adapter.notifyDataSetChanged()
            songId = PlayerConfig.music!!.song_id
            model?.getComment(songId, page, pageSize)
            GlobalScope.launch(Dispatchers.Main) {
                delay(100)
                rootView!!.rv_comment.showLoading()
            }
        }

        model?.comment?.observe(
            ctx,
            Observer {
                if (it == null)return@Observer
                when (it.code) {
                    LiveDataWrapper.CODE_OK -> {
                        page++
                        rootView?.tv_commentNum?.text = ResUtil.getString(R.string.commentNum, it.value.commentlist_last_nums)
                        if (it.value.commentlist_hot != null) {
                            commentList.addAll(it.value.commentlist_hot)
                        }
                        if (it.value.commentlist_last != null) {
                            commentList.addAll(it.value.commentlist_last)
                        }
                        adapter.notifyDataSetChanged()
                        rootView?.rv_comment?.showContent()
                    }
                    LiveDataWrapper.CODE_NO_DATA -> {
                        rootView?.tv_commentNum?.text = ctx.getString(R.string.commentNum, "0")
                        rootView?.rv_comment?.showError(ctx.getString(R.string.noData))
                    }
                    LiveDataWrapper.CODE_ERROR -> {
                        rootView?.rv_comment?.showError()
                    }
                }
            }
        )
        rootView!!.rv_comment.layoutManager = LinearLayoutManager(rootView!!.context)
        rootView!!.rv_comment.adapter = adapter
        rootView!!.rv_comment.addItemDecoration(
            DrawableItemDecoration(
                ViewUtil.dpToPx(10f), 0, ViewUtil.dpToPx(10f), 2,
                RecyclerView.VERTICAL, ctx.getDrawable(R.drawable.recycler_divider)
            )
        )
    }
    fun show() {
        init()
        dialog?.show()
    }
    fun dismiss() {
        dialog?.dismiss()
    }
    fun destory() {
        dialog = null
    }
}