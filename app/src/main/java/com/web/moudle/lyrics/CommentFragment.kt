package com.web.moudle.lyrics

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.music.m.R
import com.music.m.databinding.FragmentCommentBinding
import com.web.common.base.BaseFragment
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.web.moudle.musicEntry.adapter.CommentAdapter
import com.web.moudle.musicEntry.bean.CommentItem
import com.web.moudle.musicEntry.model.DetailMusicViewModel

class CommentFragment : BaseFragment<FragmentCommentBinding>() {
    private var songId: String = ""
    private var page: Int = 0
    private var pageSize = 30
    private var model: DetailMusicViewModel? = null
    private val commentList = ArrayList<CommentItem>()
    private val adapter = CommentAdapter(commentList)

    override fun getLayoutId(): Int = R.layout.fragment_comment

    override fun initView(rootView: View) {
        model?.comment?.observe(this, Observer {
            if (it == null) return@Observer
            when (it.code) {
                LiveDataWrapper.CODE_OK -> {
                    page++
                    binding.tvCommentNum.text = it.value.commentlist_last_nums.toString()
                    if (it.value.commentlist_hot != null) {
                        commentList.addAll(it.value.commentlist_hot)
                    }
                    if (it.value.commentlist_last != null) {
                        commentList.addAll(it.value.commentlist_last)
                    }
                    adapter.notifyDataSetChanged()
                }
                LiveDataWrapper.CODE_NO_DATA -> {}
                LiveDataWrapper.CODE_ERROR -> {}
            }
        })
        binding.rvComment.layoutManager = LinearLayoutManager(rootView.context)
        binding.rvComment.adapter = adapter
        if (songId != requireArguments().getString("songId")!!) {
            page = 0
            commentList.clear()
            adapter.notifyDataSetChanged()
            songId = requireArguments().getString("songId")!!
        }
        model?.getComment(songId, page, pageSize)
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this)[DetailMusicViewModel::class.java]
    }

    companion object {
        @JvmStatic
        fun show(v: View) {
            val pop = BasePopupWindow<FragmentCommentBinding>(
                v.context,
                LayoutInflater.from(v.context).inflate(R.layout.fragment_comment, null, false),
                ViewGroup.LayoutParams.MATCH_PARENT,
                (ViewUtil.screenHeight() * 0.6f).toInt()
            )
            pop.show(v, Gravity.BOTTOM)
        }

        fun hide() {}
    }
}