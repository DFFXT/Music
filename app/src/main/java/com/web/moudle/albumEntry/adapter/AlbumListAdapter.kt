package com.web.moudle.albumEntry.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.moudle.albumEntry.bean.OtherSong
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.videoEntry.ui.VideoEntryActivity
import com.web.web.R

class AlbumListAdapter(private val ctx:Context,private val list:ArrayList<OtherSong>?):BaseAdapter<OtherSong>(list) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: OtherSong?) {
        if(item==null)return
        holder.bindText(R.id.tv_albumName,item.title)
        holder.bindText(R.id.tv_duration,ResUtil.timeFormat("mm:ss",item.duration.toLong()*1000))

        if(TextUtils.isEmpty(item.info)){
            holder.bindText(R.id.tv_subTitle,item.author)
        }else{
            holder.bindText(R.id.tv_subTitle,item.author+" - "+item.info)
        }


        val mv=holder.findViewById<View>(R.id.tv_hasMv)
        if(item.hasMv==0){
            mv.visibility=View.GONE
        }else{
            mv.visibility=View.VISIBLE
            mv.setOnClickListener { VideoEntryActivity.actionStart(it.context,null,songId = item.songId) }
        }
        holder.itemView.setOnClickListener {
            MusicDetailActivity.actionStart(it.context,item.songId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_song,parent,false))
    }
}