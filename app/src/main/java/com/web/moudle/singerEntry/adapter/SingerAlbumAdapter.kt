package com.web.moudle.singerEntry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.web.R

class SingerAlbumAdapter(private val ctx:Context, list:ArrayList<SimpleAlbumInfo>): BaseAdapter<SimpleAlbumInfo>(list) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: SimpleAlbumInfo?) {
        holder.bindText(R.id.tv_musicName,item?.albumName)
        holder.itemView.setOnClickListener {
            AlbumEntryActivity.actionStart(it.context,item!!.albumId)
        }
        ImageLoad.load(item!!.albumImage).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_musicIcon))
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_entry_song_rect,p0,false))
    }
}