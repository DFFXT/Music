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
import com.web.moudle.musicSearch.bean.next.next.next.SimpleSongSheet
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.web.R

class SimpleSheetAdapter:PagedListAdapter<SimpleSongSheet,BaseViewHolder>(diff) {
    var itemClick:((SimpleSongSheet?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1)?:return


        val start=item.stdSheetName().indexOf(InternetMusicActivity.keyWords)
        if(start>=0){
            val end = start+ InternetMusicActivity.keyWords.length
            val spannable= SpannableString(item.stdSheetName())
            spannable.setSpan(TextAppearanceSpan(holder.itemView.context,R.style.search_focus),start,end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            holder.bindSpannable(R.id.tv_sheetName,spannable)
        }else{
            holder.bindText(R.id.tv_sheetName,item.stdSheetName())
        }

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