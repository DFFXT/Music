package com.web.moudle.singerEntry.ui

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseFragment
import com.web.common.base.BaseViewHolder
import com.web.web.R

class SongFragment:BaseFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_song
    }

    override fun initView(rootView: View) {
        val rvSongList=rootView.findViewById<RecyclerView>(R.id.rv_songList)
        rvSongList.layoutManager=LinearLayoutManager(context)
        val list=ArrayList<String>()
        for(i in 0..90){
            list.add("_______$i")
        }
        rvSongList.adapter=object :BaseAdapter<String>(list){
            override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String?) {
                holder.bindText(R.id.tv_musicName, "---$position")
            }

            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder {
                return BaseViewHolder(LayoutInflater.from(context).inflate(R.layout.item_entry_song,p0,false))
            }
        }
    }
}