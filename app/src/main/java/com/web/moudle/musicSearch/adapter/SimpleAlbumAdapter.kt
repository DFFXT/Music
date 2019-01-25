package com.web.moudle.musicSearch.adapter

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.musicSearch.bean.next.next.next.SimpleAlbumInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.web.R

class SimpleAlbumAdapter:PagedListAdapter<SimpleAlbumInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleAlbumInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1) ?: return

        var start=item.stdAlbumName().indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+ InternetMusicActivity.keyWords.length
            val spannable= SpannableString(item.stdAlbumName())
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_albumName,spannable)
        }else{
            holder.bindText(R.id.tv_albumName,item.stdAlbumName())
        }
        start=item.stdArtistName().indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+ InternetMusicActivity.keyWords.length
            val spannable= SpannableString(item.stdArtistName())
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_artistName,spannable)
        }else{
            holder.bindText(R.id.tv_artistName,item.stdArtistName())
        }

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