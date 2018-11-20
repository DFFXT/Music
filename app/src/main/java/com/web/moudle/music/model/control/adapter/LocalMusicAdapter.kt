package com.web.moudle.music.model.control.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.data.Music
import com.web.web.R


class LocalMusicAdapter(private val ctx:Context,list:List<Music>?): BaseAdapter<Music>(list) {
    var addListenner:((Int)->Unit)?=null
    var itemClickListener:((View, Int)->Unit)?=null
    var itemLongClickListener:((View,Int)->Boolean)?=null
    var index=-1
        set(v){
            if(v in 0..(itemCount - 1)){
                if(index>=0){
                    notifyItemChanged(index)
                }
                field=v
                notifyItemChanged(v)
            }else if(field in 0..(itemCount - 1)){
                val tmpIndex=field
                field=-1
                notifyItemChanged(tmpIndex)
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.music_item_list,parent,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int,item:Music?) {
        val tvMusicName=holder.bindText(R.id.musicName, item?.musicName)
        val tvSingerName=holder.bindText(R.id.singerName, item?.singer)
        holder.findViewById<ImageView>(R.id.add).setOnClickListener {
            addListenner?.invoke(position)
        }
        holder.rootView.setOnClickListener{
            itemClickListener?.invoke(it,position)
        }
        holder.rootView.setOnLongClickListener {
            itemLongClickListener?.let {listener->
                return@setOnLongClickListener listener.invoke(it,position)
            }
            return@setOnLongClickListener false
        }
        if(index==position){
            tvMusicName.setTextColor(ResUtil.getColor(R.color.themeColor))
            tvSingerName.setTextColor(ResUtil.getColor(R.color.themeColor))
        }else{
            tvMusicName.setTextColor(Color.BLACK)
            tvSingerName.setTextColor(Color.BLACK)
        }
    }
}