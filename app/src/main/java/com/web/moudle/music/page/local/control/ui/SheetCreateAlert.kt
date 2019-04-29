package com.web.moudle.music.page.local.control.ui

import android.content.Context
import android.view.View
import com.web.common.base.BaseAdapter
import com.web.common.util.ViewUtil
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.web.R
import kotlinx.android.synthetic.main.layout_create_select_sheet.view.*

/**
 * 目前用于创建歌单
 */
class SheetCreateAlert constructor(context: Context, title: String) : BaseListPopWindow<String>(
        context,
        title,
        R.layout.layout_create_select_sheet,
        width = (ViewUtil.screenWidth() * 0.5f).toInt()
) {

    var createListener:(()->Unit)?=null
    init {
        rootView.tv_addAndSave.setOnClickListener {
            createListener?.invoke()
        }
    }

    fun setItemClickListener(listener:((View?, Int)->Unit)){
        (adapter as SingleTextAdapter).itemClickListener=listener
    }

    var ad:SingleTextAdapter?=null

    override fun adapter(): BaseAdapter<String> {
        if(ad==null){
            ad=SingleTextAdapter(list)
        }
        return ad!!
    }

    override fun recyclerView(): Int =R.id.recyclerView
}