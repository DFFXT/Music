package com.web.misc

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


class GapItemDecoration @JvmOverloads constructor(private val left:Int=0,private val top:Int=0,private val right:Int=0,private val bottom:Int=0): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if(parent.adapter.itemCount-1!=parent.getChildAdapterPosition(view)){
            outRect.left=left
            outRect.top=top
            outRect.right=right
            outRect.bottom=bottom
        }
    }
}