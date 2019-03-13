package com.web.moudle.music.page.local.control.ui

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseAdapter
import com.web.common.base.log
import com.web.common.util.KeyboardManager
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.web.moudle.music.page.local.control.adapter.LocalSheetAdapter
import com.web.moudle.music.page.local.control.adapter.MyItemTouchHelperCallBack
import com.web.moudle.music.page.local.control.adapter.SimpleSelectListAdapter
import com.web.moudle.music.page.local.control.interf.ListSelectListener
import com.web.moudle.music.page.local.control.interf.LocalSheetListener
import com.web.web.R
import kotlinx.android.synthetic.main.layout_title_list.view.*


class LocalSheetListAlert(context: Context, title: String) : BaseListPopWindow<String>(
        context,
        title,
        R.layout.layout_title_list,
        (ViewUtil.screenWidth() * 0.75f).toInt()) {


    fun setIndex(index: Int) {
        (adapter as LocalSheetAdapter).setIndex(index)
    }
    fun setListSelectListener(listener:LocalSheetListener){
        (adapter as LocalSheetAdapter).setListener(listener)
    }

    override fun adapter(): BaseAdapter<String> {
        return LocalSheetAdapter(list)
    }

    override fun recyclerView(): Int {
        return R.id.recyclerView
    }

}