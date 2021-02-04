package com.web.common.tool

import android.content.Context
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.web.R

object MToast {
    @JvmStatic
    var mToast:Toast?=null
    @JvmStatic
    fun showToast(context:Context,msg:String){
        if(mToast!=null){//**取消上次的Toast
            mToast?.cancel()
        }
        mToast=Toast(context)
        val view=LayoutInflater.from(context).inflate(R.layout.layout_toast,null,false) as TextView
        mToast?.view=view
        val drawable=ResourcesCompat.getDrawable(context.resources,R.drawable.icon_waring_white,context.theme)
        drawable?.bounds= Rect(0,0,ViewUtil.dpToPx(30F),ViewUtil.dpToPx(30F))
        view.setCompoundDrawables(drawable,null,null,null)
        view.text=msg
        mToast?.setGravity(Gravity.TOP,0,ViewUtil.dpToPx(60f))
        mToast?.duration=Toast.LENGTH_SHORT
        mToast?.show()
    }
    @JvmStatic
    fun showToast(context:Context,@StringRes stringId:Int){
        MToast.showToast(context,ResUtil.getString(stringId))
    }

}