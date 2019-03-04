package com.web.misc

import android.annotation.SuppressLint
import android.app.Activity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper
import com.web.common.util.ViewUtil

@SuppressLint("ViewConstructor")
class SwipeFrameLayout constructor(ctx:Activity):FrameLayout(ctx) {

    private var mLeft=0
    private var helper:ViewDragHelper?=null
    private var canDrag=false
    private val minValidDistance=ViewUtil.screenWidth()/3
    init {
        helper=ViewDragHelper.create(this,object :ViewDragHelper.Callback(){
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return canDrag
            }

            override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
                if(left==ViewUtil.screenWidth()){
                    ctx.finish()
                    ctx.overridePendingTransition(0,0)
                }
            }

            override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
                mLeft=capturedChild.left
            }

            override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
                canDrag = edgeFlags==ViewDragHelper.EDGE_LEFT
            }


            override fun onEdgeLock(edgeFlags: Int): Boolean {
                return false
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                if(releasedChild.left>=minValidDistance){
                    helper?.settleCapturedViewAt(ViewUtil.screenWidth(),releasedChild.top)
                }else{
                    helper?.settleCapturedViewAt(0, releasedChild.top)
                }
                postInvalidate()
                canDrag=false
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return 200
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                var x=(left+dx*0.2).toInt()
                if(x<0) x=0
                return x
            }
        })
        helper?.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return helper!!.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        helper!!.processTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if(helper!!.continueSettling(true)){
            postInvalidate()
        }
    }
}