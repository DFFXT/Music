package com.web.moudle.musicSearch.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.music.m.R

class SimpleSheetAdapter:PagedListAdapter<SimpleSongSheet,BaseViewHolder>(diff) {
    var itemClick:((SimpleSongSheet?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1)?:return

        holder.bindText(R.id.tv_sheetName,
                ResUtil.getSpannable(item.stdSheetName(),
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_normal)))


        holder.bindText(R.id.tv_songCount,item.songCount)
        holder.bindText(R.id.tv_sheetCreator,item.userInfo.userName)
        holder.bindText(R.id.tv_playCount,item.playCount)
        ImageLoad.load(item.sheetIcon).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_sheetIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_sheet,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleSongSheet>(){
            override fun areItemsTheSame(p0: SimpleSongSheet, p1: SimpleSongSheet): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleSongSheet, p1: SimpleSongSheet): Boolean {
                return false
            }
        }
    }
}