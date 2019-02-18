package com.web.common.base

import android.support.design.widget.AppBarLayout

abstract class BaseAppBarLayoutOffsetChangeListener:AppBarLayout.OnOffsetChangedListener {
    companion object {
        const val STATE_COLLAPSE=0
        const val STATE_EXPANDING=1
        const val STATE_EXPANDED=2
    }
    private var state= STATE_COLLAPSE
    private var prePosition=-1
    override fun onOffsetChanged(app: AppBarLayout, position: Int) {
        val p=-position
        if(p==prePosition)return
        val total=app.totalScrollRange
        when {
            p==0 -> state= STATE_EXPANDED
            p<total -> state= STATE_EXPANDING
            p==total -> state= STATE_COLLAPSE
        }
        prePosition=p
        offsetChanged(state,p)
    }
    abstract fun offsetChanged(state:Int,position:Int)
}