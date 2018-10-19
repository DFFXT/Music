package com.web.music.model.control.adapter

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.common.util.StrUtil
import com.web.data.InternetMusic
import com.web.web.R
import java.text.DecimalFormat

class InternetMusicAdapter(private val context: Context) : PagedListAdapter<InternetMusic, BaseViewHolder>(diff) {
    var listener:OnItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.music_internet_item,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item= getItem(position) ?: return
        holder.bindText(R.id.musicName,item.musicName)
        holder.bindText(R.id.singerName,item.singerName)
        holder.bindText(R.id.size,getFileSize(item.fullSize))
        holder.bindText(R.id.tv_musicDuration,StrUtil.timeFormat("mm:ss",item.duration*1000L))
        holder.rootView.setOnClickListener {
            listener?.itemClick(item)
        }
    }
    private fun getFileSize(size:Int):String{
        val format=DecimalFormat("0.00")
        return when(size){
            in 0..1024*1024-> format.format(size/1024F)+"KB"
            else->format.format(size/1024F/1024)+"M"
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
