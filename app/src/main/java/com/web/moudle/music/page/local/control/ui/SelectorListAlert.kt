package com.web.moudle.music.page.local.control.ui

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.music.page.local.control.adapter.SimpleSelectListAdapter
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.web.web.R
import kotlinx.android.synthetic.main.layout_title_list.view.*


class SelectorListAlert(context: Context, title: String) : BasePopupWindow(
        context,
        LayoutInflater.from(context).inflate(R.layout.layout_title_list, null),
        (ViewUtil.screenWidth() * 0.75f).toInt()) {
    var list: ArrayList<String> = arrayListOf()
        set(value) {
            field.clear()
            field.addAll(value)
        }
    private var adapter: SimpleSelectListAdapter
    private var rv:RecyclerView

    init {
        rootView.tv_title.text = title
        rv = rootView.recyclerView as RecyclerView
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapter = SimpleSelectListAdapter(context, list)
        rv.adapter = adapter
    }

    fun setIndex(index: Int) {
        adapter.setIndex(index)
    }
    fun setListSelectListener(listener:ListSelectListener){
        adapter.setListener(listener)
    }
    fun setCanTouchRemove(canTouchRemove:Boolean){
        if (canTouchRemove) {
            val callback = MyItemTouchHelperCallBack(adapter)
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(rv)
        }else{
            val callback = MyItemTouchHelperCallBack()
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(rv)
        }
    }
}