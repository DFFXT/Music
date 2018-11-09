package com.web.misc

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.web.common.util.ResUtil
import com.web.web.R

class TextWithDrawable:FrameLayout{

    private var text:String?=null
    private var drawable:Drawable?=null
    private var tvTitle:TextView
    private var ivDrawable:ImageView
    constructor(context: Context?) :this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes){
        val view=LayoutInflater.from(context).inflate(R.layout.item_text_drawable,this,false)
        tvTitle=view.findViewById(R.id.tv_text)
        ivDrawable=view.findViewById(R.id.iv_rightIcon)

        val typeArray=context?.obtainStyledAttributes(attrs, R.styleable.TextWithDrawable)
        setText(typeArray?.getString(R.styleable.TextWithDrawable_textWithDrawable_text))
        typeArray?.getDrawable(R.styleable.TextWithDrawable_textWithDrawable_drawable)?.let {
            setDrawable(it)
        }

        typeArray?.recycle()
        addView(view)
        setOnClickListener {}
    }
    fun setText(text:String?){
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




}