package com.web.moudle.music.page.control.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ViewUtil
import com.web.web.R

class SingleTextAdapter (private val ctx: Context, list:List<String>?): BaseAdapter<String>(list){
    var itemClickListener:((Int)->Unit)?=null
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String?) {
        (holder.rootView as TextView).text = item
        val padding=ViewUtil.dpToPx(10f)
        holder.rootView.setPadding(padding,padding,padding,padding)
        holder.rootView.setOnClickListener {
            itemClickListener?.invoke(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.view_textview,parent,false))
    }
}