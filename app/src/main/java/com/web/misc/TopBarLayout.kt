package com.web.misc

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.web.web.R

class TopBarLayout : FrameLayout {
    private val view: View = LayoutInflater.from(context).inflate(R.layout.layout_topar, this, false)
    val startImageView: ImageView
    val endImageView: ImageView
    private val mainTitle: TextView
    private val subtitle: TextView
    private val endText: TextView
    private var mainTitleColor:Int=Color.WHITE
        set(value) {
            field=value
            endText.setTextColor(value)
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TopBarLayout)
        startImageView.setImageDrawable(ta.getDrawable(R.styleable.TopBarLayout_topBar_startImage))
        endImageView.setImageDrawable(ta.getDrawable(R.styleable.TopBarLayout_topBar_endImage))
        mainTitle.setTextColor(ta.getColor(R.styleable.TopBarLayout_topBar_mainTitleColor, Color.BLACK))
        subtitle.setTextColor(ta.getColor(R.styleable.TopBarLayout_topBar_subtitleColor, Color.BLACK))
        mainTitle.text = ta.getString(R.styleable.TopBarLayout_topBar_mainTitleText)
        endText.text = ta.getString(R.styleable.TopBarLayout_topBar_endText)
        subtitle.text = ta.getString(R.styleable.TopBarLayout_topBar_subtitleText)

        mainTitleColor=ta.getColor(R.styleable.TopBarLayout_topBar_mainTitleColor,mainTitleColor)
        if (ta.hasValue(R.styleable.TopBarLayout_topBar_tint)) {
            val tint = ColorStateList.valueOf(ta.getColor(R.styleable.TopBarLayout_topBar_tint,0))
            startImageView.imageTintList = tint
            endImageView.imageTintList = tint
        }
        ta.recycle()

        if (TextUtils.isEmpty(subtitle.text)) {
            subtitle.visibility = View.GONE
        }
    }

    init {
        startImageView = view.findViewById(R.id.img_topBarStartImage)
        endImageView = view.findViewById(R.id.img_topBarEndImage)
        mainTitle = view.findViewById(R.id.tv_topBarTitle)
        subtitle = view.findViewById(R.id.tv_topBarSubtitle)
        endText = view.findViewById(R.id.tv_endText)
        addView(view)

        //**默认点击事件
        setStartImageListener(OnClickListener {
            (context as Activity).finish()
        })
    }

    fun setStartImageListener(listener: OnClickListener?) {
        startImageView.setOnClickListener(listener)
    }

    fun setEndImageListener(listener: OnClickListener?) {
        endImageView.setOnClickListener(listener)
    }

    fun setStartImage(@DrawableRes res: Int) {
        startImageView.setImageResource(res)
    }

    fun setEndImage(@DrawableRes res: Int) {
        endImageView.setImageResource(res)
    }

    fun setMainTitle(title: String):TextView {
        mainTitle.text = title
        return mainTitle
    }

    fun setSubtitle(subtitle: String) {
        this.subtitle.text = subtitle
        this.subtitle.visibility = View.VISIBLE
    }

    fun setTint(@ColorInt color: Int) {
        val tint = ColorStateList.valueOf(color)
        startImageView.imageTintList = tint
        endImageView.imageTintList = tint
    }

    fun setMainTitleColor(@ColorInt color:Int):TextView{
        mainTitleColor=color
        mainTitle.setTextColor(color)
        return mainTitle
    }


}