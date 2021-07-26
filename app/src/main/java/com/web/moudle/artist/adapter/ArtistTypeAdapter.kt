package com.web.moudle.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ViewUtil
import com.web.moudle.artist.ArtistTypeActivity
import com.web.moudle.artist.bean.ArtistInfo
import com.web.moudle.artist.bean.ArtistType
import com.web.web.R

class ArtistTypeAdapter(data:List<ArtistType>):BaseAdapter<ArtistType>(data) {
    val padding= ViewUtil.dpToPx(30f)
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: ArtistType) {
        if (item==null)return
        holder.itemView.setPadding(0,padding,0,padding)
        holder.bindText(R.id.textView,item.typeName)
        holder.itemView.setOnClickListener {
            ArtistTypeActivity.actionStart(it.context,item.typeName,item.areaCode,item.sexCode)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_textview,parent,false))
    }
}