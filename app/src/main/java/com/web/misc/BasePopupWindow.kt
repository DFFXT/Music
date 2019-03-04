package com.web.misc

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

open class BasePopupWindow(private val ctx: Context, val rootView: View) {

    private val popupWindow: PopupWindow = PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    init {
        popupWindow.contentView = rootView
        setBackground(ColorDrawable(Color.TRANSPARENT))
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true

        popupWindow.setOnDismissListener {
            applyWindowDarkAlpha(0.5f, 1f, 300)
        }
    }


    fun enableTouchDismis(touchDismiss: Boolean) {
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
        }
    }

    private fun applyWindowDarkAlpha(from: Float, to: Float, duration: Int) {
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

    fun show(parent: View, gravity: Int, x: Int, y: Int) {
        popupWindow.update()
        popupWindow.showAtLocation(parent, gravity, x, y)
        applyWindowDarkAlpha(1f, 0.5f, 300)
    }
    fun showCenter(parent: View){
        show(parent,Gravity.CENTER,0,0)
    }

    fun dismiss() {
        popupWindow.dismiss()
    }
}