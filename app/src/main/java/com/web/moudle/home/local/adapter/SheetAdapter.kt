package com.web.moudle.home.local.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.moudle.music.player.bean.SongSheetWW
import com.web.web.R

class SheetAdapter:BaseAdapter<SongSheetWW>() {
    var itemLongClick:((View,Int)->Boolean)?=null
    var itemClick:((View,Int)->Unit)?=null
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: SongSheetWW?) {
        if(item==null)return
        holder.bindText(R.id.tv_title,item.name)
        holder.bindText(R.id.tv_musicCount,"共"+item.musicCount.toString()+"首")

        holder.itemView.setOnLongClickListener {
            return@setOnLongClickListener itemLongClick?.invoke(it,position)?:false
        }
        holder.itemView.setOnClickListener {
            itemClick?.invoke(it,position)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_my_sheet,parent,false))
    }
}