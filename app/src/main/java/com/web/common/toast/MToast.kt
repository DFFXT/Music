package com.web.common.toast

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.web.common.util.ViewUtil
import com.web.web.R

object MToast {
    @JvmStatic
    fun showToast(context:Context,msg:String){
        val mToast=Toast(context)
        val view=LayoutInflater.from(context).inflate(R.layout.layout_toast,null,false) as TextView
        mToast.view=view
        val drawable=context.resources.getDrawable(R.drawable.icon_waring_white,context.theme)
        drawable.bounds= Rect(0,0,ViewUtil.dpToPx(30F),ViewUtil.dpToPx(30F))
        view.setCompoundDrawables(drawable,null,null,null)
        view.text=msg
        mToast.setGravity(Gravity.TOP,0,ViewUtil.dpToPx(60f))
        mToast.duration=Toast.LENGTH_SHORT
        mToast.show()
    }

}