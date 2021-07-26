package com.web.moudle.songSheetEntry.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.web.R

class SheetTagAdapter(private val ctx: Context, list: List<String>?) : BaseAdapter<String>(list) {
    private val tagColor= arrayOf(ResUtil.getColor(R.color.tag_color1),
            ResUtil.getColor(R.color.pink),
            ResUtil.getColor(R.color.lightBlue),
            ResUtil.getColor(R.color.colorAccent))
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String) {
        holder.bindText(R.id.tv_tagName, item)
        val drawable = GradientDrawable()
        drawable.cornerRadius=8f
        drawable.setStroke(2,ResUtil.getColor(R.color.themeColor))
        drawable.setColor(tagColor[position%tagColor.size])
        holder.itemView.background=drawable
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_tag, p0, false))
    }
}