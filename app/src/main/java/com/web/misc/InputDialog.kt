package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import com.music.m.R
import com.music.m.databinding.LayoutInputBinding
import com.web.common.util.ViewUtil

class InputDialog (ctx:Context):BasePopupWindow<LayoutInputBinding>(
        ctx,
        LayoutInflater.from(ctx).inflate(R.layout.layout_input,null,false),
        (ViewUtil.screenWidth()*0.7f).toInt()
) {
    init {
        binding.tvLeft.setOnClickListener {
            dismiss()
        }
    }

    fun setConfirmListener(listener: ((value: String) -> Unit)): InputDialog {
        binding.tvRight.setOnClickListener {
            listener.invoke(binding.etInput.text.toString())
        }
        return this
    }

    fun setTitle(title: String): InputDialog {
        binding.tvTitle.text = title
        return this
    }

    fun setHint(hint: String): InputDialog {
        binding.etInput.hint = hint
        return this
    }
}