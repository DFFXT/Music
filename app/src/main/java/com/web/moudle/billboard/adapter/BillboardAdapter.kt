package com.web.moudle.billboard.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.moudle.billboard.bean.Content
import com.web.moudle.billboradDetail.NetMusicListActivity
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.music.m.R

class BillboardAdapter(data:List<Content>):BaseAdapter<Content>(data) {
    private val ids= arrayListOf(R.id.tv_no1,R.id.tv_no2,R.id.tv_no3)
    private val ivIds= arrayListOf(R.id.iv_top1,R.id.iv_top2,R.id.iv_top3)
    @SuppressLint("CheckResult")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: Content?) {
        val ctx=holder.itemView.context
        holder.bindText(R.id.tv_billboardName,item?.name)
        if(item==null)return
        item.content.forEachIndexed{ index, v->
            if(index>=3)return@forEachIndexed
            holder.bindText(ids[index],"${index+1}. "+v.title+" - "+v.author).setOnClickListener {
                MusicDetailActivity.actionStart(ctx,v.song_id)
            }
            holder.bindImage(ivIds[index],v.pic_small)
        }
        holder.itemView.setBackgroundColor(Color.parseColor(item.bg_color.replace("0x","#")))
        val titleColor=Color.parseColor(item.color.replace("0x","#"))
        holder.findViewById<TextView>(R.id.tv_billboardName).setTextColor(titleColor)
        //**给榜单添加右边的小标
        val title=holder.findViewById<TextView>(R.id.tv_billboardName)
        val res=ctx.resources
        val drawable=BitmapDrawable(res,ResUtil.getBitmapRotate(R.drawable.icon_back_black,180f))
        val size=res.getDimension(R.dimen.textSize_big).toInt()
        drawable.setBounds(0,0,size,size)
        drawable.setTint(titleColor)
        title.setCompoundDrawables(null,null,drawable,null)


        holder.itemView.setOnClickListener {
            NetMusicListActivity.actionStartBillboard(ctx,item.name,item.type)
        }


    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_billboard,p0,false))
    }
}