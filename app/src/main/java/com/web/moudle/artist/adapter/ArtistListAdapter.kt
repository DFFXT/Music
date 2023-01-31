package com.web.moudle.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.music.m.R

class ArtistListAdapter(data:List<ArtistInfo>):BaseAdapter<ArtistInfo>(data) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: ArtistInfo?) {
        if (item==null)return
        holder.bindImage(R.id.iv_artistIcon,item.avatar_big)
        holder.bindText(R.id.tv_artistName,item.name)
        holder.itemView.setOnClickListener {
            SingerEntryActivity.actionStart(it.context,item.ting_uid)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_artist_iv,parent,false))
    }
}