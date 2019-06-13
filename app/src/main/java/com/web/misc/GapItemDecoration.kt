package com.web.misc

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * remainEndPadding 是否保留最后一个item的gap
 * remainBottomPadding 是否保留最后一个item的gap
 */
open class GapItemDecoration @JvmOverloads constructor(private val left:Int=0, private val top:Int=0,
                                                       private val right:Int=0, private val bottom:Int=0,
                                                       private val remainLeftPadding:Boolean=false,
                                                       private val remainTopPadding:Boolean=false,
                                                       private val remainEndPadding:Boolean=false,
                                                       private val remainBottomPadding:Boolean=false): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            val lm=parent.layoutManager as LinearLayoutManager
            if(lm.orientation== RecyclerView.VERTICAL){
                if(remainTopPadding||parent.getChildAdapterPosition(view)!=0){
                    outRect.top=top
                }
                outRect.left=left
                outRect.right=right
                if(remainBottomPadding||it.itemCount-1!=parent.getChildAdapterPosition(view)){
                    outRect.bottom=bottom
                }
            }
            if(lm.orientation== RecyclerView.HORIZONTAL){
                if(remainLeftPadding||parent.getChildAdapterPosition(view)!=0){
                    outRect.left=left
                }
                outRect.top=top
                outRect.bottom=bottom
                if(remainEndPadding||it.itemCount-1!=parent.getChildAdapterPosition(view)){
                    outRect.right=right
                }
            }

        }

    }
}