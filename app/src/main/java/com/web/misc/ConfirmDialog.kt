package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.web.common.util.ViewUtil
import com.web.web.R
import kotlinx.android.synthetic.main.layout_confirm.view.*

/**
 * 确认弹窗
 * 如果设置宽度为WRAP会导致在华为note10上面宽度很小接近0
 */
class ConfirmDialog(ctx: Context) : BasePopupWindow(
    ctx,
    LayoutInflater.from(ctx).inflate(R.layout.layout_confirm, null, false),
    (ViewUtil.screenWidth() * 0.6).toInt(),
    ViewGroup.LayoutParams.WRAP_CONTENT
) {

    init {
        rootView.elevation = 5f
    }

    fun setLeftListener(listener: (ConfirmDialog) -> Unit): ConfirmDialog {
        rootView.tv_left.setOnClickListener {
            listener(this)
        }
        return this
    }

    fun setRightListener(listener: (ConfirmDialog) -> Unit): ConfirmDialog {
        rootView.tv_right.setOnClickListener {
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