package com.web.moudle.music.page.local.control.ui

import android.content.Context
import com.web.common.base.BaseAdapter
import com.web.common.util.ViewUtil
import com.web.moudle.music.page.local.control.adapter.SimpleSelectListAdapter
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.music.m.R


class SelectorListAlert(context: Context, title: String) : BaseListPopWindow<String>(
        context,
        title,
        R.layout.layout_title_list,
        (ViewUtil.screenWidth() * 0.75f).toInt()) {


    fun setIndex(index: Int) {
        (adapter as SimpleSelectListAdapter).setIndex(index)
    }
    fun setListener(listener: ListSelectListener){
        (adapter as SimpleSelectListAdapter).setListener(listener)
        listSelectListener=listener
    }

    override fun adapter(): BaseAdapter<String> {
        return SimpleSelectListAdapter(list)
    }

    override fun recyclerView(): Int =R.id.recyclerView
}