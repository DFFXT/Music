package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.web.web.R
import kotlinx.android.synthetic.main.layout_confirm.view.*

class ConfirmDialog(ctx: Context) : BasePopupWindow(ctx, LayoutInflater.from(ctx).inflate(R.layout.layout_confirm, null, false)) {

    init {
        rootView.elevation = 5f
    }

    fun setLeftListener(listener:(ConfirmDialog)->Unit): ConfirmDialog {
        rootView.tv_left.setOnClickListener{
            listener(this)
        }
        return this
    }

    fun setRightListener(listener:(ConfirmDialog)->Unit): ConfirmDialog {
        rootView.tv_right.setOnClickListener{
            listener(this)
        }
        return this
    }

    fun setMsg(msg: String): ConfirmDialog {
        rootView.tv_msg.text = msg
        return this
    }

    fun setRightText(text: String?): ConfirmDialog {
        rootView.tv_right.text = text
        return this
    }

    fun setLeftText(text: String?): ConfirmDialog {
        rootView.tv_left.text = text
        return this
    }

}