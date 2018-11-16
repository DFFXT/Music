package com.web.misc

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout

/**
 * RecyclerView添加底部或者右部间隔和分割图
 */
class DrawableItemDecoration(private val orientation: Int,private val gap: Int, private var drawable: Drawable) : GapItemDecoration(
        0,
        0,
        if (orientation == LinearLayout.HORIZONTAL) gap else 0,
        if (orientation == LinearLayout.VERTICAL) gap else 0
){
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val parentLeft = parent.paddingLeft
        val parentRight = parent.width - parent.paddingRight
        val parentTop = parent.paddingTop
        val parentBottom = parent.height - parent.paddingBottom
        if(orientation==LinearLayout.VERTICAL){
            for(index in 0 until parent.childCount){
                val lp=parent.getChildAt(index).layoutParams as RecyclerView.LayoutParams
                val top=parent.getChildAt(index).bottom + lp.bottomMargin
                drawable.setBounds(parentLeft,top,parentRight,top+gap)
                drawable.draw(c)
            }
        }else if(orientation==LinearLayout.HORIZONTAL){

            for(index in 0 until parent.childCount){
                val lp=parent.getChildAt(index).layoutParams as RecyclerView.LayoutParams
                val left=parent.getChildAt(index).right + lp.marginEnd
                drawable.setBounds(left,parentTop,left+gap,parentBottom)
                drawable.draw(c)
            }
        }
    }
}

