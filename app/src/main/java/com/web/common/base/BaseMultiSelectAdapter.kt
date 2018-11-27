package com.web.common.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import com.web.web.R
import java.util.*

abstract class BaseMultiSelectAdapter<T:Any?>(private val ctx:Context,list:List<T>?):BaseAdapter<T>(list) {
    var selectedSet=HashSet<Int>()
    var isSelectAll=false
    set(value) {
        if(field!=value){
            field=value
            if(field){
                data?.let {
                    for(i in it.indices){
                        selectedSet.add(i)
                    }
                }
            }else{
                selectedSet.clear()
            }
            notifyDataSetChanged()
        }
    }
    var isSelect=false
        set(value) {
            if(field!=value){
                field=value
                if(!value){
                    isSelectAll=false
                }
                selectedSet.clear()
                notifyDataSetChanged()
            }
        }

    /**
     * 数据绑定、此处控制checkBox显示和隐藏
     */
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: T?) {
        val cb=holder.findViewById<CheckBox>(R.id.cb_itemMultiSelect)
        if(!isSelect)
            cb.visibility=View.GONE
        else {
            cb.visibility = View.VISIBLE
            cb.isChecked=selectedSet.contains(holder.adapterPosition)
        }
        cb.setOnCheckedChangeListener{_,isChecked->
            if(isChecked)
                selectedSet.add(holder.adapterPosition)
            else
                selectedSet.remove(holder.adapterPosition)
        }
        onBindItemView(holder,position,item)
    }

    /**
     * 创建itemView，将itemView放入带CheckBox的rootView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val rootView=LayoutInflater.from(ctx).inflate(R.layout.item_multi_select_root_view,parent,false) as RelativeLayout
        val child=onCreateItemView(rootView,viewType)

        var lp= child.layoutParams
        if(lp==null){
            lp=RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
        }
        lp=lp as RelativeLayout.LayoutParams
        lp.addRule(RelativeLayout.END_OF,R.id.cb_itemMultiSelect)
        lp.addRule(RelativeLayout.ALIGN_PARENT_END)
        child.layoutParams=lp
        rootView.addView(child)
        return BaseViewHolder(rootView)
    }
    fun select(position:Int){
        selectedSet.add(position)
        notifyItemChanged(position)
    }
    fun unSelect(position:Int){
        selectedSet.remove(position)
        notifyItemChanged(position)
    }
    abstract fun onCreateItemView(parent:ViewGroup,viewType: Int):View
    abstract fun onBindItemView(holder: BaseViewHolder, position: Int, item: T?)
}