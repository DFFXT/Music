package com.web.moudle.musicSearch.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.musicSearch.bean.SimpleArtistInfo
import com.web.moudle.musicSearch.bean.SimpleSongSheet
import com.web.web.R

class SimpleSheetAdapter:PagedListAdapter<SimpleSongSheet,BaseViewHolder>(diff) {
    var itemClick:((SimpleSongSheet?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1)
        holder.bindText(R.id.tv_sheetName,item?.sheetName)
        holder.bindText(R.id.tv_songCount,item?.songCount.toString())
        holder.bindText(R.id.tv_sheetCreator,item?.sheetCreator)
        holder.bindText(R.id.tv_playCount,item?.playCount)
        ImageLoad.load(item?.sheetIcon).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_sheetIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_sheet,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleSongSheet>(){
            override fun areItemsTheSame(p0: SimpleSongSheet, p1: SimpleSongSheet): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleSongSheet, p1: SimpleSongSheet): Boolean {
                return false
            }
        }
    }
}