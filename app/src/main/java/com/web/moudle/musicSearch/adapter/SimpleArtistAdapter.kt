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
import com.web.moudle.musicSearch.bean.next.next.next.SimpleArtistInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.music.m.R

class SimpleArtistAdapter:PagedListAdapter<SimpleArtistInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleArtistInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1) ?: return


        holder.bindText(R.id.tv_artistName,
                ResUtil.getSpannable(item.stdArtistName(),
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_normal)))
        holder.bindText(R.id.tv_singleMusicNum, item.singleMusicNum)
        holder.bindText(R.id.tv_albumNum,item.albumNum)
        holder.bindText(R.id.tv_district,item.district)
        ImageLoad.load(item.artistImage).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_artistIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_artist,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleArtistInfo>(){
            override fun areItemsTheSame(p0: SimpleArtistInfo, p1: SimpleArtistInfo): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleArtistInfo, p1: SimpleArtistInfo): Boolean {
                return false
            }
        }
    }
}