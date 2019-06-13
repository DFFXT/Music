package com.web.moudle.musicDownload.adpter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.config.Shortcut
import com.web.moudle.musicDownload.bean.DownloadMusic
import com.web.web.R

class DownloadingAdapter:BaseAdapter<DownloadMusic>() {

    var click:((View,Int)->Unit)?=null

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: DownloadMusic?) {
        if(item==null)return
        val music=item.internetMusicDetail
        holder.bindText(R.id.musicName,music.songName)
        holder.bindText(R.id.hasDownload, ResUtil.getFileSize(music.hasDownload))
        holder.bindText(R.id.fullSize, ResUtil.getFileSize(music.size))
        holder.bindImage(R.id.downloadStatu, if (item.status == DownloadMusic.DOWNLOAD_DOWNLOADING) R.drawable.icon_play_black else R.drawable.icon_pause_black)
                .setOnClickListener { v -> click?.invoke(v, position) }
        holder.itemView.setOnClickListener(null)
        holder.itemView.findViewById<View>(R.id.close).setOnClickListener { v -> click?.invoke(v, position) }
        (holder.findViewById<View>(R.id.progress) as ProgressBar).progress = (music.hasDownload * 100 / music.size).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.download_listview,parent,false))
    }
}