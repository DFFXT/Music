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
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.web.R

class SimpleArtistAdapter:PagedListAdapter<SimpleArtistInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleArtistInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1) ?: return


        val start=item.stdArtistName().indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+InternetMusicActivity.keyWords.length
            val spannable=SpannableString(item.stdArtistName())
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end,Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_artistName,spannable)
        }else{
            holder.bindText(R.id.tv_artistName,item.stdArtistName())
        }

        holder.bindText(R.id.tv_singleMusicNum, item.singleMusicNum)
        holder.bindText(R.id.tv_albumNum,item.albumNum)
        holder.bindText(R.id.tv_district,item.district)
        ImageLoad.load(item.artistImage).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_artistIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_artist,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleArtistInfo>(){
            override fun areItemsTheSame(p0: SimpleArtistInfo, p1: SimpleArtistInfo): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleArtistInfo, p1: SimpleArtistInfo): Boolean {
                return false
            }
        }
    }
}