package com.web.moudle.music.page.local.control.ui

import android.content.Context
import com.web.common.base.BaseAdapter
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.moudle.music.page.local.control.adapter.LocalSheetAdapter
import com.web.moudle.music.page.local.control.interf.LocalSheetListener
import com.music.m.R


class LocalSheetListAlert(context: Context, title: String) : BaseListPopWindow<String>(
        context,
        title,
        R.layout.layout_title_list,
        width=(ViewUtil.screenWidth() * 0.75f).toInt(),
        maxHeight = (ViewUtil.screenHeight() * 0.5f).toInt()
        ) {

    init {
        setCanTouchRemove(true)
    }

    fun setIndex(index: Int) {
        (adapter as LocalSheetAdapter).setIndex(index)
    }
    fun setListener(listener: LocalSheetListener){
        (adapter as LocalSheetAdapter).setListener(listener)
        listSelectListener=listener
    }

    override fun adapter(): BaseAdapter<String> {
        return LocalSheetAdapter(list)
    }

    override fun recyclerView(): Int {
        return R.id.recyclerView
    }

}