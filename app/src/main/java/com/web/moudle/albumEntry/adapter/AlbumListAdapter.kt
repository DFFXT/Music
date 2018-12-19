package com.web.moudle.albumEntry.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.moudle.albumEntry.bean.OtherSong
import com.web.moudle.musicEntry.ui.MusicDetailActivity

import com.web.web.R

class AlbumListAdapter(private val ctx:Context,private val list:ArrayList<OtherSong>):BaseAdapter<OtherSong>(list) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: OtherSong?) {
        val songInfo=list[position]
        holder.bindText(R.id.tv_albumName,songInfo.title+" - "+songInfo.version)
        holder.bindText(R.id.tv_duration,ResUtil.timeFormat("mm:ss",songInfo.duration.toLong()*1000))
        if(songInfo.hasMv==0){
            holder.findViewById<View>(R.id.tv_hasMv).visibility=View.GONE
        }else{
            holder.findViewById<View>(R.id.tv_hasMv).visibility=View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            MusicDetailActivity.actionStart(it.context,songInfo.songId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_song,parent,false))
    }
}