package com.web.misc

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ColorPicker @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
        View(context, attrs, defStyleAttr, defStyleRes) {
    private var paint: Paint = Paint()
    var colorArray:IntArray = IntArray(2)
    var positionArray:FloatArray= FloatArray(2)
    private var bitmap:Bitmap?=null
    private var canvas:Canvas?=null
    var listener: ((Int?) -> Unit)? = null
    private lateinit var gradient:LinearGradient

    init {
        colorArray[0]=Color.RED
        colorArray[1]=Color.BLUE
        positionArray[0]=0f
        positionArray[1]=1f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gradient=LinearGradient(0f,0f,w.toFloat(),0f,colorArray,positionArray,Shader.TileMode.CLAMP)
        bitmap?.recycle()
        bitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_4444)
        canvas= Canvas(bitmap)
    }
    override fun onDraw(c: Canvas?) {
        if(c==null)return
        paint.shader=gradient
        canvas?.drawRect(0f,0f,width.toFloat(),height.toFloat(),paint)
        c.drawBitmap(bitmap,0f,0f,null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            bitmap?.let {bitmap->
                if(event.x>0&&event.x<bitmap.width&&event.y>0&&event.y<bitmap.height){
                    val color=bitmap.getPixel(event.x.toInt(),event.y.toInt())
                    listener?.invoke(color)
                }
            }
        }
        return true
    }


}