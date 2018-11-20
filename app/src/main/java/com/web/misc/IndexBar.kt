package com.web.misc

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.web.common.util.ResUtil
import com.web.web.R

class IndexBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        View(context, attrs, defStyleAttr, defStyleRes) {

    var selectListener:((Int)->Unit)?=null
    var touchUpListener:((Int)->Unit)?=null
    var indexList:List<String>?=null
        set(value) {
            field=value
            requestLayout()
        }
    private var widthList=ArrayList<Int>()
    private var lineHeight=-1f
    private var maxWidth:Int=0
    var verticalGap=20
        set(value) {
            field=value
            invalidate()
        }
    var textSize=30f
        set(value) {
            field=value
            paint.textSize=value
            lineHeight=-paint.fontMetrics.ascent
            requestLayout()
        }
    var textColor:Int=Color.parseColor("#55000000")
        set(value) {
            field=value
            invalidate()
        }
    var textSelectedColor:Int=ResUtil.getColor(R.color.themeColor)
        set(value) {
            field=value
            invalidate()
        }
    private val paint= Paint()

    init {
        paint.textSize=textSize
        paint.isAntiAlias=true
        lineHeight=-paint.fontMetrics.ascent


        val typedArray=context.obtainStyledAttributes(attrs, R.styleable.IndexBar)

        textColor=typedArray.getColor(R.styleable.IndexBar_indexBar_textColor,textColor)
        textSelectedColor=typedArray.getColor(R.styleable.IndexBar_indexBar_selectTextColor,textSelectedColor)
        textSize=typedArray.getDimension(R.styleable.IndexBar_indexBar_textSize,textSize)
        typedArray.recycle()

    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        indexList?.let {
            var top=paddingTop.toFloat()
            for(i in it.indices){

                if(i==selectedIndex){
                    paint.color=textSelectedColor
                }else{
                    paint.color=textColor
                }
                top+=lineHeight
                canvas.drawText(it[i],paddingStart+(maxWidth-widthList[i])/2f,top,paint)

                top+=verticalGap
            }
        }
    }


    var selectedIndex=-1
        set(value) {
            field=value
            invalidate()
        }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val y=e.y
        indexList?.let {
            var index=(y/(lineHeight+verticalGap)).toInt()
            if(index<0)index=0
            else if(index>=it.size)index=it.size-1
            if(selectedIndex!=index){
                selectedIndex=index
                //invalidate()
                selectListener?.invoke(selectedIndex)

            }
        }
        if(e.action==MotionEvent.ACTION_UP){
            //invalidate()
            touchUpListener?.invoke(selectedIndex)
        }

        return true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var height=paddingTop+paddingBottom
        var width=0

        widthList.clear()
        indexList?.forEach{
            val wd=paint.measureText(it).toInt()
            widthList.add(wd)
            width=Math.max(wd,width)
        }
        maxWidth=width
        width+=paddingStart+paddingEnd
        indexList?.let {
            height+=it.size*(verticalGap-paint.fontMetricsInt.ascent)-verticalGap-paint.fontMetricsInt.bottom
        }
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        var hMS=heightMeasureSpec
        var wMS=widthMeasureSpec
        if(heightMode==MeasureSpec.AT_MOST){
            hMS=MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY)
        }
        if(widthMode==MeasureSpec.AT_MOST){
            wMS=MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY)
        }

        super.onMeasure(wMS, hMS)
    }

}