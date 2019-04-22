package com.web.moudle.musicSearch.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
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

        holder.bindText(R.id.tv_musicName,
                ResUtil.getSpannable(item.musicName,
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_normal)))
        holder.bindText(R.id.tv_singerName,
                ResUtil.getSpannable(item.author,
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_small)))

        holder.findViewById<View>(R.id.tv_singerName).setOnClickListener {
            SingerEntryActivity.actionStart(it.context,item.uid)
        }


        holder.bindText(R.id.tv_albumName,item.albumTitle).setOnClickListener {
            AlbumEntryActivity.actionStart(it.context,item.albumId)
        }
        ImageLoad.load(item.picSmall).into(holder.findViewById(R.id.iv_musicIcon))
        //holder.bindText(R.itemId.size,ResUtil.getFileSize(item.size))
        //holder.bindText(R.itemId.tv_musicDuration,ResUtil.timeFormat("mm:ss",item.duration*1000L))
        holder.itemView.setOnClickListener {
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
