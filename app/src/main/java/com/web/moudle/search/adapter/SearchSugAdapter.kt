package com.web.moudle.search.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ResUtil
import com.web.moudle.albumEntry.ui.AlbumEntryActivity
import com.web.moudle.musicEntry.ui.MusicDetailActivity
import com.web.moudle.search.bean.SearchResItem
import com.web.moudle.singerEntry.ui.SingerEntryActivity
import com.web.moudle.songSheetEntry.ui.SongSheetActivity
import com.web.web.R

class SearchSugAdapter(searchSug: List<SearchResItem>) : BaseAdapter<SearchResItem>(searchSug) {
    var search:((keyword:String)->Unit)?=null
    var clearAllHistory:(()->Unit)?=null
    var itemClick:((item:SearchResItem,position:Int)->Unit)?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val v:View = if(viewType!=SearchResItem.SearchItemType_Head){
            LayoutInflater.from(parent.context).inflate(R.layout.search_sug_item, parent, false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_head,parent,false)
        }
        return BaseViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return data!![position].type
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int,item:SearchResItem) {
        if(item==null)return
        when (item.type) {
            SearchResItem.SearchItemType_Music -> {//**歌曲
                holder.bindText(R.id.tv_name, item.name + " -- " + item.subName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.music_sug_icon)
                holder.itemView.setOnClickListener {
                    item.saveOrUpdateAsync()
                    MusicDetailActivity.actionStart(it.context,item.itemId)
                    itemClick?.invoke(item,position)
                }
            }
            SearchResItem.SearchItemType_Album -> {//**专辑
                holder.bindText(R.id.tv_name, item.name + " --  " + item.subName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.album_sug_icon)
                holder.itemView.setOnClickListener {
                    item.saveOrUpdateAsync()
                    AlbumEntryActivity.actionStart(it.context,item.itemId)
                    itemClick?.invoke(item,position)
                }
            }
            SearchResItem.SearchItemType_Artist -> {//**歌手
                holder.bindText(R.id.tv_name, item.name)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.singer_sug_icon)
                holder.itemView.setOnClickListener {
                    item.saveOrUpdateAsync()
                    SingerEntryActivity.actionStart(it.context,item.itemId)
                    itemClick?.invoke(item,position)
                }
            }
            SearchResItem.SearchItemType_Sheet ->{
                holder.bindText(R.id.tv_name, item.name)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.def_song_sheet_icon)
                holder.itemView.setOnClickListener {
                    item.saveOrUpdateAsync()
                    SongSheetActivity.actionStart(it.context,item.itemId)
                    itemClick?.invoke(item,position)
                }
            }
            SearchResItem.SearchItemType_Head ->{
                holder.bindText(R.id.tv_name,item.name)
                val iv=holder.findViewById<ImageView>(R.id.searchSug_op)
                iv.setImageResource(R.drawable.clear)
                iv.imageTintList= ColorStateList.valueOf(ResUtil.getColor(R.color.textColor_9))
                iv.setOnClickListener {
                    clearAllHistory?.invoke()
                }
            }
            SearchResItem.SearchItemType_Search->{
                val v=holder.findViewById<View>(R.id.searchSug_type)
                v.setBackgroundResource(R.drawable.icon_search)
                v.backgroundTintList= ColorStateList.valueOf(ResUtil.getColor(R.color.textColor_9))
                holder.bindText(R.id.tv_name,item.name)
                holder.itemView.setOnClickListener {
                    search?.invoke(item.name)
                    itemClick?.invoke(item,position)
                }


            }
        }
    }
}