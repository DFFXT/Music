package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.music.m.R
import com.music.m.databinding.LayoutConfirmBinding
import com.web.common.util.ViewUtil

/**
 * 确认弹窗
 * 如果设置宽度为WRAP会导致在华为note10上面宽度很小接近0
 */
class ConfirmDialog(ctx: Context) : BasePopupWindow<LayoutConfirmBinding>(
    ctx,
    LayoutInflater.from(ctx).inflate(R.layout.layout_confirm, null, false),
    (ViewUtil.screenWidth() * 0.6).toInt(),
    ViewGroup.LayoutParams.WRAP_CONTENT
) {

    init {
        binding.root.elevation = 5f
    }

    fun setLeftListener(listener: (ConfirmDialog) -> Unit): ConfirmDialog {
        binding.tvLeft.setOnClickListener {
            listener(this)
        }
        return this
    }

    fun setRightListener(listener: (ConfirmDialog) -> Unit): ConfirmDialog {
        binding.tvRight.setOnClickListener {
            listener(this)
        }
        return this
    }

    fun setMsg(msg: String): ConfirmDialog {
        binding.tvMsg.text = msg
        return this
    }

    fun setRightText(text: String?): ConfirmDialog {
        binding.tvRight.text = text
        return this
    }

    fun setLeftText(text: String?): ConfirmDialog {
        binding.tvLeft.text = text
        return this
    }
}