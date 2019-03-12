package com.web.moudle.music.page.local.control.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.web.misc.DrawableItemDecoration
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.web.R
import kotlinx.android.synthetic.main.layout_title_list.view.*

/**
 * 目前用于创建歌单
 */
class SingleTextListAlert constructor(context: Context, title: String) : BasePopupWindow(
        ctx = context,
        rootView=LayoutInflater.from(context).inflate(R.layout.layout_title_list, null),
        width = (ViewUtil.screenWidth() * 0.5f).toInt()
) {

    var list: ArrayList<String> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }
    val adapter: SingleTextAdapter

    init {
        rootView.tv_title.text=title
        val view = rootView.recyclerView
        view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        view.addItemDecoration(DrawableItemDecoration(orientation = LinearLayout.VERTICAL,bottom = 4,drawable = ResUtil.getDrawable(R.drawable.recycler_divider)))
        adapter = SingleTextAdapter(context, list)
        view.adapter = adapter
    }
    fun setItemClickListener(listener:((Int)->Unit)){
        adapter.itemClickListener=listener
    }


}