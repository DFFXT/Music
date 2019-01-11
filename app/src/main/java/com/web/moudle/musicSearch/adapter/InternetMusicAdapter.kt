package com.web.moudle.musicSearch.adapter

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.moudle.musicSearch.bean.SimpleMusicInfo
import com.web.web.R

class InternetMusicAdapter(private val context: Context) : PagedListAdapter<SimpleMusicInfo, BaseViewHolder>(diff) {
    var listener: OnItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_music,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item= getItem(position) ?: return
        holder.bindText(R.id.musicName,item.songTitle)
        holder.bindText(R.id.singerName,item.artist)
        //holder.bindText(R.id.size,ResUtil.getFileSize(item.size))
        //holder.bindText(R.id.tv_musicDuration,ResUtil.timeFormat("mm:ss",item.duration*1000L))
        holder.rootView.setOnClickListener {
            listener?.itemClick(item)
        }
    }


    companion object {
        private val diff = object : DiffUtil.ItemCallback<SimpleMusicInfo>() {
            override fun areItemsTheSame(oldItem: SimpleMusicInfo, newItem: SimpleMusicInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: SimpleMusicInfo, newItem: SimpleMusicInfo): Boolean {
                return false
            }
        }
    }
    interface OnItemClickListener{
        fun itemClick(music:SimpleMusicInfo)
    }
}
