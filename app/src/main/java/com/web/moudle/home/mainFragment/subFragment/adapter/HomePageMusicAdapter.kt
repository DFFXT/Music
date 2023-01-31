package com.web.moudle.home.mainFragment.subFragment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.moudle.home.mainFragment.subFragment.bean.HomePageMusic
import com.web.moudle.home.mainFragment.subFragment.bean.SongSheetItem
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.songSheetEntry.ui.SongSheetActivity
import com.web.moudle.videoEntry.ui.VideoEntryActivity
import com.music.m.R

class HomePageMusicAdapter:BaseAdapter<HomePageMusic>() {
    var itemClick:((item:HomePageMusic,index:Int)->Unit)?=null
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: HomePageMusic) {
        if(item==null)return
        holder.bindImage(R.id.iv_musicIcon,R.drawable.def_song_sheet_icon,item.pic_big)
        holder.bindText(R.id.tv_musicName,item.title)
        holder.bindText(R.id.tv_singerName,item.author)
        holder.bindText(R.id.tv_albumName,item.album_title)
        val mv=holder.findViewById<View>(R.id.tv_hasMv)
        if(item.has_mv==1){
            mv.visibility=View.VISIBLE
            mv.setOnClickListener {
                VideoEntryActivity.actionStart(it.context,"",item.song_id)
            }
        }
        holder.itemView.setOnClickListener {
            mv.visibility=View.INVISIBLE
            mv.setOnClickListener(null)
        }
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item,position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_page_music,parent,false))
    }
}