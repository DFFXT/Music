package com.web.common.util.help

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.web.common.base.BaseAdapter

fun <T> RecyclerView.setOnItemClickListener(listener:(Int,T)->Unit){
    val detector = GestureDetector(this.context,object :GestureDetector.SimpleOnGestureListener(){
        @Suppress("UNCHECKED_CAST")
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            findChildViewUnder(e.x,e.y)?.let {
                val position = getChildAdapterPosition(it)
                listener(position,(adapter as BaseAdapter<*>).data[position] as T)
            }
            return false
        }
    })
    this.addOnItemTouchListener(object :RecyclerView.OnItemTouchListener{
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return detector.onTouchEvent(e)
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    })
}