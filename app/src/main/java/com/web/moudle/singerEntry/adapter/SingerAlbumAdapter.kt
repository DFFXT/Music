package com.web.moudle.singerEntry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.singerEntry.bean.AlbumEntryItem
import com.web.web.R

class SingerAlbumAdapter(private val ctx:Context, list:ArrayList<AlbumEntryItem>): BaseAdapter<AlbumEntryItem>(list) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: AlbumEntryItem?) {
        holder.bindText(R.id.tv_musicName,item?.albumName)
        holder.rootView.setOnClickListener {
            AlbumEntryActivity.actionStart(it.context,item!!.albumId)
        }
        ImageLoad.load(item!!.pic90).into(holder.findViewById(R.id.iv_musicIcon))
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_entry_song_rect,p0,false))
    }
}