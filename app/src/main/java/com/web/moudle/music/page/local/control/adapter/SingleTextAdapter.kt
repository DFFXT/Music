package com.web.moudle.music.page.local.control.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseViewHolder
import com.web.common.util.ViewUtil
import com.web.web.R

/**
 * 仅仅是单一text展示，没有其他功能
 */
open class SingleTextAdapter (list:List<String>?): BaseAdapter<String>(list){
    var selectIndex=-1
    var selectRender:((holder:BaseViewHolder,index:Int)->Unit)?=null
    var commonRender:((holder:BaseViewHolder,index:Int)->Unit)?=null
    var itemClickListener:((Int)->Unit)?=null
    private val padding=ViewUtil.dpToPx(10f)
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: String?) {
        (holder.itemView as TextView).text = item
        if(position==selectIndex){
            selectRender?.invoke(holder,position)
        }else{
            commonRender?.invoke(holder,position)
        }
        holder.itemView.setPadding(padding,padding,padding,padding)
        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(position)
            if(selectIndex>=0){
                val pre=selectIndex
                notifyItemChanged(pre)
            }
            if(selectRender!=null){
                selectIndex=position
                notifyItemChanged(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_textview,parent,false))
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun reset(){
        selectIndex=-1
    }
}