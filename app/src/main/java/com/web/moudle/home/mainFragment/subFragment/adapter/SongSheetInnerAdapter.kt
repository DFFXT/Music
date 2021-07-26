package com.web.moudle.home.mainFragment.subFragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.moudle.home.mainFragment.subFragment.bean.SongSheetItem
import com.web.moudle.songSheetEntry.ui.SongSheetActivity
import com.web.web.R

class SongSheetInnerAdapter():BaseAdapter<SongSheetItem>() {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: SongSheetItem) {
        if(item==null)return
        holder.bindImage(R.id.iv_sheetIcon,R.drawable.def_song_sheet_icon,item.list_pic_small)
        holder.bindText(R.id.tv_sheetName,item.title)
        holder.bindText(R.id.tv_tag,item.tag)
        holder.bindText(R.id.tv_musicNum,item.song_num.toString())
        holder.itemView.setOnClickListener {
            SongSheetActivity.actionStart(it.context,item.list_id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_song_sheet_inner,parent,false))
    }
}