package com.web.moudle.musicEntry.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.moudle.musicEntry.bean.CommentItem
import com.web.web.R

class CommentAdapter(data:List<CommentItem>):BaseAdapter<CommentItem>(data) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: CommentItem?) {
        if(item==null)return
        holder.bindImage(R.id.iv_author,R.drawable.def_user_icon,item.author.userpic_small)
        holder.bindText(R.id.tv_author,item.author.username)
        holder.bindText(R.id.tv_time,ResUtil.timeFormat("MM-dd HH:mm",item.ctime*1000))
        holder.bindText(R.id.tv_comment,item.comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false))
    }
}