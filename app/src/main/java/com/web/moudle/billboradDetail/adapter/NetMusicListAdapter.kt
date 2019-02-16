package com.web.moudle.billboradDetail.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.web.R

class NetMusicListAdapter(data:List<SimpleMusicInfo>?): BaseAdapter<SimpleMusicInfo>(data) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: SimpleMusicInfo?) {
        ImageLoad.load(item?.picSmall).into(holder.findViewById(R.id.iv_musicIcon))
        holder.bindText(R.id.tv_musicName,item?.musicName)
        holder.bindText(R.id.tv_singerName,item?.author)
        holder.bindText(R.id.tv_albumName,item?.albumTitle)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_sheet_song,p0,false))
    }
}