package com.web.misc

import android.content.Context
import android.view.LayoutInflater
import com.web.web.R

class LoadingWindow(val ctx:Context):BasePopupWindow(ctx,LayoutInflater.from(ctx).inflate(R.layout.layout_pop_loading,null,false)) {
    init {
        this.enableTouchDismiss(false)
    }
}