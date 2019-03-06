package com.web.misc

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.web.web.R

class RoundImageView @JvmOverloads constructor(
        context: Context, private val attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

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


    private val path=Path()
    private val rect=RectF()
    override fun onDraw(canvas: Canvas) {
        setPath()
        canvas.clipPath(path)
        super.onDraw(canvas)
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