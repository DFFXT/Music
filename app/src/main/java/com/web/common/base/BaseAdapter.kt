package com.web.common.base

import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<T>(var data: List<T>?) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun getItemCount(): Int{
        data?.let {
            return it.size
        }
        return 0
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        onBindViewHolder(holder,position,data?.get(position))
    }
    abstract fun onBindViewHolder(holder: BaseViewHolder, position: Int,item:T?)
}