package com.web.common.base

import android.content.Context
import android.util.SparseBooleanArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.web.web.R

abstract class BaseMultiSelectAdapter<T:Any?>(private val ctx:Context,list:List<T>?):BaseAdapter<T>(list) {
    var selectedSet=SparseBooleanArray()
    var isSelectAll=false
    set(value) {
        if(field!=value){
            field=value
            if(field){
                data?.let {
                    for(i in it.indices){
                        selectedSet.put(i,true)
                    }
                }
            }else{
                selectedSet.clear()
            }
            notifyItemRangeChanged(0,itemCount)
        }
    }
    var isSelect=false
        set(value) {
            if(field!=value){
                field=value
                selectedSet.clear()
                if(!value){
                    isSelectAll=false
                }
                notifyItemRangeChanged(0,itemCount)
            }
        }

    /**
     * 数据绑定、此处控制checkBox显示和隐藏
     */
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: T?) {
        val cb=holder.findViewById<View>(R.id.cb_itemMultiSelect)
        if(!isSelect)
            cb.visibility=View.GONE
        else {
            cb.visibility = View.VISIBLE
            cb.isSelected=selectedSet[holder.adapterPosition,false]
        }
        cb.setOnClickListener {
            it.isSelected=!it.isSelected
            selectedSet.put(holder.adapterPosition,it.isSelected)
        }
        onBindItemView(holder,position,item)
    }

    /**
     * 创建itemView，将itemView放入带CheckBox的rootView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val rootView=LayoutInflater.from(ctx).inflate(R.layout.item_multi_select_root_view,parent,false) as ViewGroup
        val child=onCreateItemView(rootView,viewType)

        var lp= child.layoutParams
        if(lp==null){
            lp=LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
        }
        lp=lp as LinearLayout.LayoutParams
        lp.gravity=Gravity.CENTER_VERTICAL
        child.layoutParams=lp
        rootView.addView(child)
        return BaseViewHolder(rootView)
    }
    fun select(position:Int){
        selectedSet.put(position,true)
        notifyItemChanged(position)
    }
    fun unSelect(position:Int){
        selectedSet.delete(position)
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    abstract fun onCreateItemView(parent:ViewGroup,viewType: Int):View
    abstract fun onBindItemView(holder: BaseViewHolder, position: Int, item: T?)
}