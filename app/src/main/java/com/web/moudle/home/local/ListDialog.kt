package com.web.moudle.home.local

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.BasePopupWindow
import com.music.m.R

class ListDialog(private val ctx:Context):BottomSheetDialog(ctx) {
    private val textList=ArrayList<String>()
    private val click=ArrayList<View.OnClickListener?>()
    private var init=false
    private val rootView:View = LayoutInflater.from(ctx).inflate(R.layout.layout_list_dialog,null,false)

    init {
        rootView as ViewGroup
        rootView.removeAllViews()
        setContentView(rootView)
    }

    fun addItem(text:String,onClickListener: View.OnClickListener?=null):ListDialog{
        textList.add(text)
        click.add(onClickListener)
        return this
    }

    override fun show() {

        if(!init){
            for(i in textList.indices){
                val tv=TextView(ctx)
                tv.text = textList[i]
                tv.setOnClickListener(click[i])
                tv.setTextColor(ResUtil.getColor(R.color.textColor_3))
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,ResUtil.getSize(R.dimen.textSize_big).toFloat())
                tv.gravity=Gravity.CENTER
                tv.setPadding(0,ViewUtil.dpToPx(20f),0,ViewUtil.dpToPx(20f))
                tv.foreground=ResUtil.getDrawable(R.drawable.selector_transparent_gray)
                if(i==textList.size-1){
                    tv.background=ColorDrawable(0)
                }else{
                    tv.setBackgroundResource(R.drawable.border_bottom_1px)
                }
                (rootView as ViewGroup).addView(tv)
            }
            init=true
        }
        super.show()
    }


}