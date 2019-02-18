package com.web.moudle.musicSearch.adapter

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.support.v7.util.DiffUtil
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.web.moudle.videoEntry.ui.VideoEntryActivity
import com.web.web.R

class InternetMusicAdapter(private val context: Context) : PagedListAdapter<SimpleMusicInfo, BaseViewHolder>(diff) {
    var listener: OnItemClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sheet_song,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item= getItem(position) ?: return
        var start=item.musicName.indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+ InternetMusicActivity.keyWords.length
            val spannable= SpannableString(item.musicName)
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_musicName,spannable)
        }else{
            holder.bindText(R.id.tv_musicName,item.musicName)
        }
        start=item.author.indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+ InternetMusicActivity.keyWords.length
            val spannable= SpannableString(item.author)
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_singerName,spannable)
        }else{
            holder.bindText(R.id.tv_singerName,item.author)
        }
        holder.findViewById<View>(R.id.tv_singerName).setOnClickListener {
            SingerEntryActivity.actionStart(it.context,item.uid)
        }


        holder.bindText(R.id.tv_albumName,item.albumTitle).setOnClickListener {
            AlbumEntryActivity.actionStart(it.context,item.albumId)
        }
        ImageLoad.load(item.picSmall).into(holder.findViewById(R.id.iv_musicIcon))
        //holder.bindText(R.id.size,ResUtil.getFileSize(item.size))
        //holder.bindText(R.id.tv_musicDuration,ResUtil.timeFormat("mm:ss",item.duration*1000L))
        holder.rootView.setOnClickListener {
            listener?.itemClick(item)
        }

        //**
        val mv=holder.findViewById<View>(R.id.tv_hasMv)
        if(item.hasMV==1) {
            mv.visibility = View.VISIBLE
            mv.setOnClickListener {
                VideoEntryActivity.actionStart(it.context,songId = item.songId)
            }
        }
        else {
            mv.visibility=View.INVISIBLE
            mv.setOnClickListener(null)
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
        fun itemClick(music: SimpleMusicInfo)
    }
}
