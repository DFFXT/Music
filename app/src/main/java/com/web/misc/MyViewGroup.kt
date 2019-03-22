package com.web.misc

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.web.misc.imageDraw.IOnMeasure

class MyViewGroup @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyleAttr: Int=0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation=LinearLayout.VERTICAL
    }

    var measureListener:IOnMeasure?=null
    private val measure= IntArray(2)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measure[0]=widthMeasureSpec
        measure[1]=heightMeasureSpec
        measureListener?.onMeasure(measure)
        super.onMeasure(measure[0], measure[1])
    }
}