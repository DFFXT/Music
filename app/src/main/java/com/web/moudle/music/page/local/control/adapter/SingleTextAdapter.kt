package com.web.moudle.music.page.local.control.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ViewUtil
import com.web.web.R

open class SingleTextAdapter (private val ctx: Context, list:List<String>?): BaseAdapter<String>(list){
    var itemClickListener:((Int)->Unit)?=null
    private val padding=ViewUtil.dpToPx(10f)
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String?) {
        if(position!=itemCount-1){
            (holder.rootView as TextView).text = item
        }
        holder.rootView.setPadding(padding,padding,padding,padding)
        holder.rootView.setOnClickListener {
            itemClickListener?.invoke(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if(viewType!=itemCount-1){
            return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.view_textview,parent,false))
        }
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_center_img,parent,false))

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}