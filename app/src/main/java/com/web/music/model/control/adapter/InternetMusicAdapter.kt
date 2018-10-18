package com.web.music.model.control.adapter

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.data.InternetMusic
import com.web.web.R

class InternetMusicAdapter(private val context: Context) : PagedListAdapter<InternetMusic, BaseViewHolder>(diff) {
    var listener:OnItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.music_internet_item,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item= getItem(position) ?: return
        holder.bindText(R.id.musicName,item.musicName)
        holder.bindText(R.id.singerName,item.singerName)
        holder.bindText(R.id.size,item.fullSize.toString())
        holder.rootView.setOnClickListener {
            listener?.itemClick(item)
        }
    }

    companion object {
        private val diff = object : DiffUtil.ItemCallback<InternetMusic>() {
            override fun areItemsTheSame(oldItem: InternetMusic, newItem: InternetMusic): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: InternetMusic, newItem: InternetMusic): Boolean {
                return false
            }
        }
    }
    interface OnItemClickListener{
        fun itemClick(music:InternetMusic)
    }
}
