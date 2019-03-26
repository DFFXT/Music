package com.web.common.base

abstract class BaseAdapter<T>(var data: List<T>?) : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {
    override fun getItemCount(): Int{
        data?.let {
            return it.size
        }
        return 0
    }

    fun update(data:List<T>){
        this.data=data
        notifyDataSetChanged()
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        onBindViewHolder(holder,position,data?.get(position))
    }
    abstract fun onBindViewHolder(holder: BaseViewHolder, position: Int,item:T?)
}