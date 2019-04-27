package com.web.misc

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.web.misc.imageDraw.IOnMeasure

class MyRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var measureListener:IOnMeasure?=null
    private val measure= IntArray(2)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measure[0]=widthMeasureSpec
        measure[1]=heightMeasureSpec
        measureListener?.onMeasure(measure)
        super.onMeasure(measure[0], measure[1])
    }
}