package com.web.moudle.music.page.local.control.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.web.R

class IndexBarAdapter : BaseAdapter<Char>() {
    private var index = -1
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: Char?) {
        val tv = holder.findViewById<TextView>(R.id.tv_title)
        tv.text = item.toString()
        if (position == index) {
            tv.setTextColor(ResUtil.getColor(R.color.white))
            tv.backgroundTintList = ColorStateList.valueOf(ResUtil.getColor(R.color.themeColor))
        } else {
            tv.setTextColor(ResUtil.getColor(R.color.textColor_6))
            tv.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_char_index, parent, false))
    }

    fun setSelectedIndex(index: Int) {
        if (index != this.index && index in data.indices) {
            if (this.index in data.indices) {
                notifyItemChanged(this.index)
            }
            this.index = index
            notifyItemChanged(index)
        }
    }

    fun setSelectChar(char: Char?) {
        if (char == null) {
            setSelectedIndex(data.size - 1)
        } else {
            val newIndex = data.indexOf(char.toUpperCase())
            if (newIndex < 0) {
                setSelectedIndex(data.size - 1)
            } else {
                setSelectedIndex(newIndex)
            }

        }
    }
}