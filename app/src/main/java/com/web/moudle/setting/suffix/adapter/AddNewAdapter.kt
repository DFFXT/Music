package com.web.moudle.setting.suffix.adapter

import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.base.OnItemClickListener
import com.music.m.R

/**
 * 新增adapter
 */
class AddNewAdapter : BaseAdapter<String>() {
    var onClickListener: OnItemClickListener<String> ? = null
    init {
        data = listOf("新 建")
    }
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String) {
        holder.bindText(R.id.tv_text, item)
        holder.itemView.setOnClickListener {
            onClickListener?.itemClick(item, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_filter_add_new
    }
}