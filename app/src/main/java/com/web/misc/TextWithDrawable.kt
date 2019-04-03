package com.web.misc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import com.web.common.util.ResUtil
import com.web.web.R

class TextWithDrawable @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var text:CharSequence?=null
    private var drawable:Drawable?=null
    private var tvTitle:TextView
    private var ivDrawable:ImageView
    private var paint=Paint()
    var bottomLine=true
        set(value) {
            field=value
            invalidate()
        }
    @ColorInt
    var bottomColor= resources.getColor(R.color.textColor_9,context.theme)
        set(value) {
            field=value
            paint.color=value
            invalidate()
        }

    init {
        val view=LayoutInflater.from(context).inflate(R.layout.item_text_drawable,this,false)
        tvTitle=view.findViewById(R.id.tv_text)
        ivDrawable=view.findViewById(R.id.iv_rightIcon)
        val typeArray=context.obtainStyledAttributes(attrs, R.styleable.TextWithDrawable)
        setText(typeArray?.getString(R.styleable.TextWithDrawable_textWithDrawable_text))
        bottomLine=typeArray.getBoolean(R.styleable.TextWithDrawable_textWithDrawable_bottomLine,bottomLine)
        bottomColor=typeArray.getColor(R.styleable.TextWithDrawable_textWithDrawable_bottomColor,bottomColor)
        typeArray?.getDrawable(R.styleable.TextWithDrawable_textWithDrawable_drawable)?.let {
            setDrawable(it)
        }
        typeArray?.recycle()
        addView(view)
        setOnClickListener {}
    }
    fun setText(text:CharSequence?){
        this.text=text
        tvTitle.text=text
    }
    fun setDrawable(drawable: Drawable){
        this.drawable=drawable
        ivDrawable.setImageDrawable(drawable)
    }
    fun setDrawableRes(resId:Int){
        setDrawable(ResUtil.getDrawable(resId))
    }


    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if(bottomLine)
            canvas.drawLine(0f,height-2f,width.toFloat(),height-2f,paint)
    }


}