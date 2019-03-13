package com.web.moudle.music.page.local.control.ui

import android.content.Context
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseAdapter
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.web.R
import kotlinx.android.synthetic.main.layout_title_list.view.*

/**
 * 目前用于创建歌单
 */
class SingleTextListAlert constructor(context: Context, title: String) : BaseListPopWindow<String>(
        context,
        title,
        R.layout.layout_title_list,
        width = (ViewUtil.screenWidth() * 0.5f).toInt()
) {


    fun setItemClickListener(listener:((Int)->Unit)){
        (adapter as SingleTextAdapter).itemClickListener=listener
    }


    override fun adapter(): BaseAdapter<String> {
        return SingleTextAdapter(list)
    }

    override fun recyclerView(): Int =R.id.recyclerView
}