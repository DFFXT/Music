package com.web.moudle.home.video.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.moudle.home.video.bean.FeedData
import com.web.moudle.videoEntry.ui.VideoEntryActivity
import com.web.web.R

class VideoRecommendAdapter:BaseAdapter<FeedData>() {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: FeedData?) {
        if(item==null)return
        holder.bindImage(R.id.iv_videoIcon,item.content.pic_large)
        holder.bindText(R.id.tv_title,item.title)
        holder.bindText(R.id.tv_peopleToSee,ResUtil.getString(R.string.xPeopleToSee,item.review_num))
        holder.itemView.setOnClickListener {
            VideoEntryActivity.actionStart(it.context,item.url)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_video_recommend,parent,false))
    }
}