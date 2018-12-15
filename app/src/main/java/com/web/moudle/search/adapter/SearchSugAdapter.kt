package com.web.moudle.search.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.moudle.entry.ui.MusicDetailActivity
import com.web.moudle.search.bean.SearchSug
import com.web.web.R

class SearchSugAdapter(var searchSug: SearchSug) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, p: Int): BaseViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_sug_item, parent, false)
        return BaseViewHolder(v)
    }

    override fun getItemCount(): Int {
        return searchSug.albumList.size + searchSug.artistList.size + searchSug.musicSugList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, p: Int) {
        when (p) {
            in 0 until searchSug.musicSugList.size -> {//**歌曲
                val item=searchSug.musicSugList[p]
                holder.bindText(R.id.tv_name, item.songName + " -- " + item.artistName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.music_sug_icon)
                holder.itemView.setOnClickListener {
                    MusicDetailActivity.actionStart(it.context,item.songId)
                }
            }
            in searchSug.musicSugList.size until searchSug.musicSugList.size + searchSug.albumList.size -> {//**专辑
                val relativeP = p - searchSug.musicSugList.size
                holder.bindText(R.id.tv_name, searchSug.albumList[relativeP].albumName + " --  " + searchSug.albumList[relativeP].artistName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.album_sug_icon)
            }
            in itemCount - searchSug.artistList.size until itemCount -> {//**歌手
                val relativeP = p - (itemCount - searchSug.artistList.size)
                holder.bindText(R.id.tv_name, searchSug.artistList[relativeP].artistName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundResource(R.drawable.singer_sug_icon)
            }
        }
    }
}