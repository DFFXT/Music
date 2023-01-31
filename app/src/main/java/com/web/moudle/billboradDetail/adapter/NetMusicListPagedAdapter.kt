package com.web.moudle.billboradDetail.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.web.common.base.BaseViewHolder
import com.web.common.imageLoader.glide.ImageLoad
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.music.m.R

class NetMusicListPagedAdapter(diff:DiffUtil.ItemCallback<SimpleMusicInfo>):PagedListAdapter<SimpleMusicInfo,BaseViewHolder>(diff) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.item_sheet_song,p0,false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, p1: Int) {
        val item=getItem(p1)
        ImageLoad.load(item?.picSmall).into(holder.findViewById(R.id.iv_musicIcon))
        holder.bindText(R.id.tv_musicName,item?.musicName)
        holder.bindText(R.id.tv_singerName,makeSpannable(item!!)).movementMethod= LinkMovementMethod.getInstance()
        holder.bindText(R.id.tv_albumName,item.albumTitle).setOnClickListener {
            AlbumEntryActivity.actionStart(it.context,item.albumId)
        }

        holder.itemView.setOnClickListener {
            MusicDetailActivity.actionStart(it.context,item.songId)
        }
    }
    //**给不同的歌手添加点击事件
    private fun makeSpannable(item:SimpleMusicInfo): Spannable {
        val singerArr=item.author.split(",")
        val idArr=item.allUid!!.split(",")
        val spannable= SpannableStringBuilder(item.author)
        for(i in idArr.indices){
            val start=getStartIndex(singerArr,i)
            spannable.setSpan(object : ClickableSpan(){
                override fun onClick(widget: View) {
                    SingerEntryActivity.actionStart(widget.context,idArr[i])
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText=false
                }
            },start,start+singerArr[i].length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        return spannable
    }
    private fun getStartIndex(aar:List<String>,index:Int):Int{
        var start=0
        for(i in 0 until index){
            start+=aar[i].length+1
        }
        return start
    }

    class Diff:DiffUtil.ItemCallback<SimpleMusicInfo>(){
        override fun areItemsTheSame(p0: SimpleMusicInfo, p1: SimpleMusicInfo): Boolean {
            return false
        }

        override fun areContentsTheSame(p0: SimpleMusicInfo, p1: SimpleMusicInfo): Boolean {
            return false
        }
    }
}