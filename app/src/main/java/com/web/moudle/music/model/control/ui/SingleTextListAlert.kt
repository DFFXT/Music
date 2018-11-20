package com.web.moudle.music.model.control.ui

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.DrawableItemDecoration
import com.web.moudle.music.model.control.adapter.SingleTextAdapter
import com.web.web.R

class SingleTextListAlert {
    private val context: Context
    private var dialog: AlertDialog? = null
    private var builder: AlertDialog.Builder
    var list: List<String>? = null
    private lateinit var view: RecyclerView
    var itemClickListener: ((Int)->Unit)? = null
    private var adapter: SingleTextAdapter? = null
    constructor(context: Context, title:String) {
        this.context = context
        builder = AlertDialog.Builder(context, R.style.Alert)
        if(!TextUtils.isEmpty(title))
            builder.setTitle(title)


    }

    fun getAdapter(): SingleTextAdapter {
        return adapter!!
    }

    fun build() {
        view = LayoutInflater.from(context).inflate(R.layout.view_recycler, null) as RecyclerView
        view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.addItemDecoration(DrawableItemDecoration(LinearLayout.VERTICAL,4,ResUtil.getDrawable(R.drawable.recycler_divider)))
        adapter = SingleTextAdapter(context, list)
        adapter!!.itemClickListener=itemClickListener
        view.adapter = adapter

        dialog = builder.create()
        dialog!!.setView(view, 5, 5, 5, 5)


    }



    @JvmOverloads
    fun show(gravity: Int= Gravity.CENTER) {
        dialog!!.show()
        val window = dialog!!.window
        if (window != null) {
            val params = window.attributes
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = (ViewUtil.screenWidth() * 0.5f).toInt()
            params.gravity=gravity
            window.attributes = params
        }
    }

    fun cancel() {
        dialog!!.cancel()
    }
}