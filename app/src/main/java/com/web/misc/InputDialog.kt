package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import com.web.common.util.ViewUtil
import com.web.web.R
import kotlinx.android.synthetic.main.layout_input.view.*

class InputDialog (ctx:Context):BasePopupWindow(
        ctx,
        LayoutInflater.from(ctx).inflate(R.layout.layout_input,null,false),
        (ViewUtil.screenWidth()*0.7f).toInt()
) {
    init {
        rootView.tv_left.setOnClickListener {
            dismiss()
        }
    }

    fun setConfirmListener(listener:((value:String)->Unit)):InputDialog{
        rootView.tv_right.setOnClickListener {
            listener.invoke(rootView.et_input.text.toString())
        }
        return this
    }
    fun setTitle(title:String):InputDialog{
        rootView.tv_title.text=title
        return this
    }
    fun setHint(hint:String):InputDialog{
        rootView.et_input.hint=hint
        return this
    }


}