package com.web.moudle.music.page.local.control.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseAdapter
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.music.page.local.control.adapter.SimpleSelectListAdapter
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.web.moudle.music.page.local.control.interf.RemoveItemListener
import com.web.web.R
import kotlinx.android.synthetic.main.layout_title_list.view.*


abstract class BaseListPopWindow<T> @JvmOverloads constructor(
        context: Context,
        title: String,
        @LayoutRes layout:Int,
        width:Int=ViewGroup.LayoutParams.WRAP_CONTENT,
        height:Int=ViewGroup.LayoutParams.WRAP_CONTENT)
    : BasePopupWindow(
        context,
        LayoutInflater.from(context).inflate(layout, null),
        width,
        height) {
    var list: ArrayList<T> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }
    private var rv:RecyclerView
    var listSelectListener:ListSelectListener?=null
    val adapter:BaseAdapter<T>

    init {
        rootView.tv_title.text = title
        rv = rootView.findViewById(this.recyclerView()) as RecyclerView
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        this.adapter=this.adapter()
        rv.adapter= adapter
    }

    fun setCanTouchRemove(canTouchRemove:Boolean){
        if (canTouchRemove) {
            val callback = MyItemTouchHelperCallBack(RemoveItemListener { holder, _ ->
                listSelectListener?.remove(holder.itemView,holder.adapterPosition)
                list.removeAt(holder.adapterPosition)
                adapter.notifyItemRemoved(holder.adapterPosition)
            })
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(rv)
        }else{
            val callback = MyItemTouchHelperCallBack()
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(rv)
        }
    }
    abstract fun adapter():BaseAdapter<T>
    @IdRes
    abstract fun recyclerView(): Int



}