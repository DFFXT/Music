package com.web.moudle.setting.suffix.adapter

import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.music.m.R

/**
 * 标题item
 */
class SuffixTitleAdapter(title: String) : BaseAdapter<String>(listOf(title)) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String) {
        holder.bindText(R.id.tv_title, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent, R.layout.item_filter_description)
    }
}