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
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet
import com.web.moudle.musicSearch.bean.next.next.next.SimpleVideoInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.web.R

class VideoSheetAdapter:PagedListAdapter<SimpleVideoInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleVideoInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1)?:return


        val str=item.stdVideoName()
        var start=str.indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+ InternetMusicActivity.keyWords.length
            val spannable= SpannableString(str)
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_videoName,spannable)
        }else{
            holder.bindText(R.id.tv_videoName,str)
        }

        val str1=item.stdVideoArtistName()
        if(str1!=null){
            start=str1.indexOf(InternetMusicActivity.keyWords)
            if(start>=0){
                val end = start+ InternetMusicActivity.keyWords.length
                val spannable= SpannableString(str1)
                spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                holder.bindSpannable(R.id.tv_videoArtist,spannable)
            }else{
                holder.bindText(R.id.tv_videoArtist,str1)
            }
        }



        ImageLoad.load(item.thumbnail).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_videoIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_video,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleVideoInfo>(){
            override fun areItemsTheSame(p0: SimpleVideoInfo, p1: SimpleVideoInfo): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleVideoInfo, p1: SimpleVideoInfo): Boolean {
                return false
            }
        }
    }
}