package com.web.moudle.artist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.web.web.R

class HotArtistAdapter:BaseAdapter<ArtistInfo>() {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: ArtistInfo?) {
        if (item==null)return
        holder.bindImage(R.id.iv_artistIcon,R.drawable.singer_default_icon,item.avatar_big)
        if(item.ting_uid!=null){
            holder.bindText(R.id.tv_artistName,item.name)
                    .setBackgroundColor(0)
        }

        holder.itemView.setOnClickListener {
            if(item.ting_uid==null)return@setOnClickListener
            SingerEntryActivity.actionStart(it.context,item.ting_uid)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_artist,parent,false))
    }
}