package com.web.misc

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View


open class GapItemDecoration @JvmOverloads constructor(private val left:Int=0,private val top:Int=0,private val right:Int=0,private val bottom:Int=0): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            val lm=parent.layoutManager as LinearLayoutManager
            if(lm.orientation==LinearLayoutManager.VERTICAL){
                if(parent.getChildAdapterPosition(view)!=0){
                    outRect.top=top
                }
                outRect.left=left
                outRect.right=right
                if(it.itemCount-1!=parent.getChildAdapterPosition(view)){
                    outRect.bottom=bottom
                }
            }
            if(lm.orientation==LinearLayoutManager.HORIZONTAL){
                if(parent.getChildAdapterPosition(view)!=0){
                    outRect.left=left
                }
                outRect.top=top
                outRect.bottom=bottom
                if(it.itemCount-1!=parent.getChildAdapterPosition(view)){
                    outRect.right=right
                }
            }

        }

    }
}