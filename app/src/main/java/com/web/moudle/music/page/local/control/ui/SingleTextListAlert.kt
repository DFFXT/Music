package com.web.moudle.music.page.local.control.ui

import android.content.Context
import com.web.common.base.BaseAdapter
import com.web.common.util.ViewUtil
import com.web.moudle.music.page.local.control.adapter.SingleTextAdapter
import com.web.web.R

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

    var ad:SingleTextAdapter?=null

    override fun adapter(): BaseAdapter<String> {
        if(ad==null){
            ad=SingleTextAdapter(list)
        }
        return ad!!
    }

    override fun recyclerView(): Int =R.id.recyclerView
}