package com.web.moudle.songSheetEntry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.web.moudle.songSheetEntry.bean.Songlist
import com.web.web.R

class SongSheetListAdapter(private val ctx:Context,list:List<Songlist>): BaseAdapter<Songlist>(list) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: Songlist) {
        if(item==null)return
        val iv:ImageView=holder.findViewById(R.id.iv_musicIcon)
        ImageLoad.load(item.pic_s130).into(iv)
        holder.bindText(R.id.tv_musicName, item.title)
        holder.bindText(R.id.tv_singerName, item.author).setOnClickListener {
            SingerEntryActivity.actionStart(it.context,item.ting_uid)
        }
        holder.bindText(R.id.tv_albumName, item.album_title).setOnClickListener {
            AlbumEntryActivity.actionStart(it.context,item.album_id)
        }
        holder.itemView.setOnClickListener {
            MusicDetailActivity.actionStart(it.context, item.song_id)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_sheet_song,p0,false))
    }
}