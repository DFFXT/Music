package com.web.moudle.musicSearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ResUtil
import com.web.moudle.musicSearch.bean.next.next.next.SimpleVideoInfo
import com.web.moudle.musicSearch.ui.InternetMusicActivity
import com.web.web.R

class SimpleVideoAdapter:PagedListAdapter<SimpleVideoInfo,BaseViewHolder>(diff) {
    var itemClick:((SimpleVideoInfo?)->Unit)?=null
    override fun onBindViewHolder(holder : BaseViewHolder, p1: Int) {
        val item= getItem(p1)?:return


        holder.bindText(R.id.tv_videoName,
                ResUtil.getSpannable(item.stdVideoName(),
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_big)))
        holder.bindText(R.id.tv_videoArtist,
                ResUtil.getSpannable(item.stdVideoArtistName(),
                        InternetMusicActivity.keyWords,
                        ResUtil.getColor(R.color.themeColor),
                        ResUtil.getSize(R.dimen.textSize_min)))


        ImageLoad.load(item.thumbnail).placeholder(R.drawable.singer_default_icon).into(holder.findViewById(R.id.iv_videoIcon))
        holder.itemView.setOnClickListener {
            itemClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_search_video,p0,false))
    }
    companion object {
        private val diff=object:DiffUtil.ItemCallback<SimpleVideoInfo>(){
            override fun areItemsTheSame(p0: SimpleVideoInfo, p1: SimpleVideoInfo): Boolean {
                return true
            }

            override fun areContentsTheSame(p0: SimpleVideoInfo, p1: SimpleVideoInfo): Boolean {
                return false
            }
        }
    }
}