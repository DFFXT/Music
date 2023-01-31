package com.web.misc

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import com.music.m.R

class RippleImageView : RoundImageView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RippleImageView)
        var color = Color.parseColor("#CBCBCB")
        color = ta?.getColor(R.styleable.RippleImageView_ripple_rippleColor, color) ?: color
        val mask: Drawable? = ta?.getDrawable(R.styleable.RippleImageView_ripple_mask)
        val d = RippleDrawable(ColorStateList.valueOf(color), drawable, mask)
        setImageDrawable(d)
        ta?.recycle()
    }
}