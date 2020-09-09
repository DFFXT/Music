package com.web.common.base

abstract class BaseAdapter<T>(var t: List<T>?=null) : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {
    var data = t?: arrayListOf()
    override fun getItemCount(): Int{
        return data.size
    }

    fun update(data:List<T>?){
        this.data=data?: emptyList()
        notifyDataSetChanged()
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        onBindViewHolder(holder,position, data[position])
    }
    abstract fun onBindViewHolder(holder: BaseViewHolder, position: Int,item:T?)
}