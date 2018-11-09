package com.web.common.base

import android.support.v7.widget.RecyclerView

abstract class BaseAdapter(var data: List<Any>) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun getItemCount(): Int {
        return data.size
    }
}