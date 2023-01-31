package com.web.moudle.musicSearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.music.m.R

class SimpleAlbumAdapter:PagedListAdapter<SimpleAlbumInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleAlbumInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1) ?: return

        holder.bindText(R.id.tv_albumName,
                ResUtil.getSpannable(item.stdAlbumName(),
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_normal)))
        holder.bindText(R.id.tv_artistName,
                ResUtil.getSpannable(item.stdArtistName(),
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_min)))

        holder.bindText(R.id.tv_publishTime,item.publishTime)
        ImageLoad.load(item.albumImage).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_artistIcon))
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