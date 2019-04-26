package com.web.misc

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.PopupWindow
import com.web.misc.imageDraw.MaxSizeOnMeasure

open class BasePopupWindow @JvmOverloads constructor (
        private val ctx: Context,
        val rootView: View,
        width:Int=ViewGroup.LayoutParams.WRAP_CONTENT,
        height:Int=ViewGroup.LayoutParams.WRAP_CONTENT,
        maxWidth:Int=ViewGroup.LayoutParams.WRAP_CONTENT,
        maxHeight:Int=ViewGroup.LayoutParams.WRAP_CONTENT) {

    private val popupWindow: PopupWindow = PopupWindow(width, height)
    private var enableWindowDark=true
    var dismissCallback:(()->Unit)?=null
    init {
        val viewGroup=MyViewGroup(ctx)
        viewGroup.addView(rootView)
        viewGroup.measureListener=MaxSizeOnMeasure(maxWidth,maxHeight)
        popupWindow.contentView = viewGroup
        setBackground(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true

        popupWindow.setOnDismissListener {
            applyWindowDarkAlpha(0.5f, 1f, 300)
            dismissCallback?.invoke()
        }
    }

    fun enableWindowDark(enable:Boolean){
        enableWindowDark=enable
    }

    fun enableTouchDismiss(touchDismiss: Boolean):BasePopupWindow{
        if (!touchDismiss) {
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = false

            rootView.isFocusable = true
            rootView.isFocusableInTouchMode = true
            rootView.setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            popupWindow.setTouchInterceptor(View.OnTouchListener { _, event ->
                val x = event.x.toInt()
                val y = event.y.toInt()
                if (event.action == MotionEvent.ACTION_DOWN && (x < 0 || x >= rootView.width || y < 0 || y >= rootView.height)) {
                    return@OnTouchListener true
                } else if (event.action == MotionEvent.ACTION_OUTSIDE) {
                    return@OnTouchListener true
                }
                false
            })
        }
        return this
    }

    private fun applyWindowDarkAlpha(from: Float, to: Float, duration: Int) {
        if(!enableWindowDark)return
        val window = (ctx as Activity).window
        val lp = window.attributes
        val animator = ValueAnimator.ofFloat(from, to)
        animator.duration = duration.toLong()
        animator.addUpdateListener { animation ->
            lp.alpha = animation.animatedValue as Float
            window.attributes = lp
        }
        animator.start()
    }

    fun setBackground(drawable: Drawable) {
        popupWindow.setBackgroundDrawable(drawable)
    }


    @JvmOverloads
    fun show(parent: View, gravity: Int=Gravity.CENTER, x: Int=0, y: Int=0) {
        popupWindow.update()
        popupWindow.showAtLocation(parent, gravity, x, y)
        applyWindowDarkAlpha(1f, 0.5f, 300)
    }
    fun showCenter(parent: View){
        show(parent,Gravity.CENTER,0,0)
    }

    open fun dismiss() {
        popupWindow.dismiss()
    }

}