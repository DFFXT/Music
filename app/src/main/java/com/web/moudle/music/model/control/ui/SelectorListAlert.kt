package com.web.moudle.music.model.control.ui

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.moudle.music.model.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.music.model.control.adapter.SimpleSelectListAdapter
import com.web.moudle.music.model.control.interf.ListSelectListener
import com.web.web.R

class SelectorListAlert {
    private val context: Context
    private var dialog: AlertDialog? = null
    private var builder: AlertDialog.Builder
    var list: List<String>? = null
    private var view: RecyclerView? = null
    var listSelectListener: ListSelectListener? = null
    private var index = -1
    var canTouchRemove=false
    private var adapter: SimpleSelectListAdapter? = null
    constructor(context: Context,title:String) {
        this.context = context
        builder = AlertDialog.Builder(context, R.style.Alert)
        builder.setTitle(title)


    }

    fun setIndex(index: Int) {
        this.index = index
        if (adapter != null) {
            adapter!!.setIndex(index)
        }
    }

    fun getAdapter(): SimpleSelectListAdapter? {
        return adapter
    }

    fun build() {
        view = LayoutInflater.from(context).inflate(R.layout.view_recycler, null) as RecyclerView
        view!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = SimpleSelectListAdapter(context, list)
        adapter!!.setListener(listSelectListener)
        adapter!!.setIndex(index)
        view!!.adapter = adapter
        if(canTouchRemove){
            val callback = MyItemTouchHelperCallBack(adapter)
            val helper = ItemTouchHelper(callback)
            helper.attachToRecyclerView(view)
        }

        dialog = builder.create()
        dialog!!.setView(view, 5, 5, 5, 5)


    }



    @JvmOverloads
    fun show(gravity: Int=Gravity.CENTER) {
        dialog!!.show()
        val window = dialog!!.window
        if (window != null) {
            val params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = (ViewUtil.screenWidth() * 0.75f).toInt()
            params.gravity=gravity
            window.attributes = params
        }
    }

    fun cancel() {
        dialog!!.cancel()
    }
}