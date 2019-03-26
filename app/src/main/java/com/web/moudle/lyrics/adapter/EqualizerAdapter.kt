package com.web.moudle.lyrics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.base.onSeekTo
import com.web.moudle.lyrics.bean.SoundInfo
import com.web.web.R

class EqualizerAdapter(data:List<SoundInfo>):BaseAdapter<SoundInfo>(data) {
    var seekToListener:((index:Short, to:Short)->Unit)?=null
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: SoundInfo?) {
        holder.bindText(R.id.tv_title,item!!.title)
        val bar=holder.findViewById<SeekBar>(R.id.seekBar)
        bar.max= item.max-item.min
        bar.progress= item.value
        bar.onSeekTo {
            item.value=it
            seekToListener?.invoke(position.toShort(),it.toShort())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
     return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sound_setting,parent,false))
    }
}