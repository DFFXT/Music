package com.web.misc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView
import com.web.misc.imageDraw.ImageDraw
import com.web.web.R

class RoundImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    var beforeDraw:ImageDraw?=null
    var afterDraw:ImageDraw?=null

    var radius=0f
        set(value) {
            if(value==field)return
            field=value
            requestLayout()
        }
    var type=0
        set(value) {
            if(value==field)return
            field=value
            requestLayout()
        }
    init {
        if(attrs!=null){
            val at=context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
            radius=at.getDimension(R.styleable.RoundImageView_iv_radius,radius)
            type=at.getInt(R.styleable.RoundImageView_iv_type,type)
            at.recycle()
        }

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        beforeDraw?.destroy()
        afterDraw?.destroy()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        beforeDraw?.onCreate()
        afterDraw?.onCreate()
    }


    private val path=Path()
    private val rect=RectF()
    override fun onDraw(canvas: Canvas) {
        beforeDraw?.draw(this,canvas)
        setPath()
        canvas.clipPath(path)
        super.onDraw(canvas)
        afterDraw?.draw(this,canvas)
    }

    private fun setPath(){
        path.reset()

        rect.set(paddingStart.toFloat(),paddingTop.toFloat(),width-paddingStart-paddingEnd.toFloat(),height-paddingTop-paddingBottom.toFloat())
        if(type==0){
            path.addRoundRect(rect,radius,radius,Path.Direction.CW)
        }else if (type==1){
            path.addRoundRect(rect,(width-paddingStart-paddingEnd)/2f,(height-paddingTop-paddingBottom)/2f,Path.Direction.CW)
        }

    }

}