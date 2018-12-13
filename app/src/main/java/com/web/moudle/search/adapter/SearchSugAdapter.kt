package com.web.moudle.search.adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseViewHolder
import com.web.moudle.search.bean.SearchSug
import com.web.web.R

class SearchSugAdapter(data: SearchSug) : RecyclerView.Adapter<BaseViewHolder>() {
    var searchSug = data
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
                holder.bindText(R.id.tv_name, searchSug.musicSugList[p].songName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundColor(Color.RED)
            }
            in searchSug.musicSugList.size until searchSug.musicSugList.size + searchSug.albumList.size -> {//**专辑
                val relativeP = p - searchSug.musicSugList.size
                holder.bindText(R.id.tv_name, searchSug.albumList[relativeP].albumName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundColor(Color.BLUE)
            }
            in itemCount - searchSug.artistList.size until itemCount -> {//**歌手
                val relativeP = p - (itemCount - searchSug.artistList.size)
                holder.bindText(R.id.tv_name, searchSug.artistList[relativeP].artistName)
                holder.findViewById<View>(R.id.searchSug_type).setBackgroundColor(Color.GREEN)
            }
        }
    }
}