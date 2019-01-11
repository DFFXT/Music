package com.web.moudle.musicSearch.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.musicSearch.bean.SimpleAlbumInfo
import com.web.moudle.musicSearch.bean.SimpleArtistInfo
import com.web.web.R

class SimpleAlbumAdapter:PagedListAdapter<SimpleAlbumInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleAlbumInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1)
        holder.bindText(R.id.tv_artistName,item?.artistName)
        holder.bindText(R.id.tv_albumName,item?.albumName)
        holder.bindText(R.id.tv_publishTime,item?.publishTime)
        ImageLoad.load(item?.albumImage).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_artistIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_album,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleAlbumInfo>(){
            override fun areItemsTheSame(p0: SimpleAlbumInfo, p1: SimpleAlbumInfo): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleAlbumInfo, p1: SimpleAlbumInfo): Boolean {
                return false
            }
        }
    }
}