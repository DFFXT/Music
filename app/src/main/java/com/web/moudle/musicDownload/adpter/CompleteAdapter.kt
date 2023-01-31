package com.web.moudle.musicDownload.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.config.Shortcut
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.music.m.R

class CompleteAdapter:BaseAdapter<DownloadMusic>() {

    var click:((View,Int)->Unit)?=null

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: DownloadMusic) {
        if(item==null)return
        val music=item.internetMusicDetail
        if (!Shortcut.isStrictEmpty(music.albumName)) {
            holder.bindText(R.id.tv_albumName, music.albumName!! + "  - ")
        }
        holder.bindText(R.id.musicName,music.songName)
        holder.bindText(R.id.tv_singerName, music.artistName)
        holder.findViewById<View>(R.id.iv_play).setOnClickListener { v -> click?.invoke(v, position) }

        holder.itemView.findViewById<View>(R.id.item_parent).setOnClickListener { v -> click?.invoke(v, position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_download_complete,parent,false))
    }
}