package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import com.music.m.R
import com.music.m.databinding.LayoutPopLoadingBinding

class LoadingWindow(val ctx:Context):BasePopupWindow<LayoutPopLoadingBinding>(ctx,LayoutInflater.from(ctx).inflate(R.layout.layout_pop_loading,null,false)) {
    init {
        this.enableTouchDismiss(false)
    }
}