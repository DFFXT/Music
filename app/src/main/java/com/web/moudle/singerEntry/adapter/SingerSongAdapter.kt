package com.web.moudle.singerEntry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.singerEntry.bean.SongEntryItem
import com.web.web.R

class SingerSongAdapter(private val ctx:Context,private val list:ArrayList<SongEntryItem>): BaseAdapter<SongEntryItem>(list) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: SongEntryItem?) {
        holder.bindText(R.id.tv_musicName,item?.title)
        holder.rootView.setOnClickListener {
            MusicDetailActivity.actionStart(it.context,item!!.songId)
        }
        ImageLoad.load(item!!.picSmall).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_musicIcon))
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_entry_song_rect,p0,false))
    }
}