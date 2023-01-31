package com.web.moudle.music.page.local.control.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.web.common.base.BaseMultiSelectAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.data.IgnoreMusic
import com.web.data.Music
import com.music.m.R


class LocalMusicAdapter(private val ctx:Context,list:List<Music>?): BaseMultiSelectAdapter<Music>(ctx,list) {
    var addListener:((View,Int)->Unit)?=null
    var itemClickListener:((View, Int)->Unit)?=null
    var itemLongClickListener:((View,Int)->Boolean)?=null
    var toggleLike:((Music,Int)->Boolean)?=null
    var index=-1
        set(v){
            if(v in 0 until itemCount){
                if(index>=0){
                    notifyItemChanged(index)
                }
                field=v
                notifyItemChanged(v)
            }else if(field in 0 until itemCount){
                val tmpIndex=field
                field=-1
                notifyItemChanged(tmpIndex)
            }
        }

    override fun onCreateItemView(parent: ViewGroup, viewType: Int): View {
        return LayoutInflater.from(ctx).inflate(R.layout.music_local_item_qiuck,parent,false)
    }

    override fun onBindItemView(holder: BaseViewHolder, position: Int, item: Music) {
        item?:return
        val tvMusicName=holder.bindText(R.id.musicName, item.musicName)
        val tvSingerName=holder.bindText(R.id.singerName, item.singer+if(item.album!=null) " - "+item.album else "")
        val ivLike=holder.findViewById<ImageView>(R.id.iv_love)
        val tvDuration = holder.findViewById<TextView>(R.id.tv_musicDuration)

        ivLike.isSelected=item.isLike
        tvDuration.text = ResUtil.timeFormat("mm:ss",item.duration.toLong())
        ivLike.setOnClickListener {
            if(isSelect){
                toggleSelect(position)
                return@setOnClickListener
            }
            toggleLike?.invoke(item,position)

        }
        holder.findViewById<ImageView>(R.id.add).setOnClickListener {
            if(isSelect){
                toggleSelect(position)
                return@setOnClickListener
            }
            addListener?.invoke(it,position)
        }
        holder.itemView.setOnClickListener{
            if(isSelect){
                toggleSelect(position)
                return@setOnClickListener
            }
            itemClickListener?.invoke(it,position)
        }
        holder.itemView.setOnLongClickListener {
            if(isSelect)return@setOnLongClickListener false
            itemLongClickListener?.let {listener->
                return@setOnLongClickListener listener.invoke(it,position)
            }
            return@setOnLongClickListener false
        }
        when {
            index==position -> {
                tvMusicName.setTextColor(ResUtil.getColor(R.color.themeColor))
                tvSingerName.setTextColor(ResUtil.getColor(R.color.themeColor))
            }
            IgnoreMusic.isIgnoreMusic(item) -> {
                tvMusicName.setTextColor(ResUtil.getColor(R.color.textColor_9))
                tvSingerName.setTextColor(ResUtil.getColor(R.color.textColor_9))
                tvDuration.setTextColor(ResUtil.getColor(R.color.textColor_9))
            }
            else -> {
                tvMusicName.setTextColor(ResUtil.getColor(R.color.textColor_3))
                tvSingerName.setTextColor(ResUtil.getColor(R.color.textColor_3))
                tvDuration.setTextColor(ResUtil.getColor(R.color.textColor_3))
            }
        }
    }

    override fun getSelectType(position: Int): Int =TYPE_LEFT_SELECTOR
}