package com.web.misc

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.web.common.base.BaseAnimatorListener
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.web.R
import kotlin.math.max
import kotlin.math.min

/**
 * 可扩展的模拟TextView
 */
class ExpandableTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var paint: TextPaint = TextPaint()
    private var staticLayout: StaticLayout? = null
    private var mWidth = 0
    private val bottomDrawableHeight = ViewUtil.dpToPx(25f)
    private var requestMeasure=true

    var textSize: Float = ViewUtil.dpToPx(16f).toFloat()
    var textColor = Color.BLACK
    var text: String? = null
        set(value) {
            field = value
            requestMeasure=true
            setText()
            requestLayout()
        }
    var bottomColor = ResUtil.getColor(R.color.themeColor)
    private var arrow: Drawable
    var minLines = 3
    var isShowFull = true
    private var fullHeight = 0
    private var hideHeight = 0
    private val touchControl = MotionEventControl()
    private var expandAnimator: ValueAnimator? = null
    private var hideAnimator: ValueAnimator? = null

    init {
        paint.textSize = textSize
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        textSize = ta.getFloat(R.styleable.ExpandableTextView_attr_textSize, textSize)
        textColor = ta.getColor(R.styleable.ExpandableTextView_attr_color, textColor)
        text = ta.getString(R.styleable.ExpandableTextView_attr_text)
        arrow = ResUtil.getDrawable(R.drawable.refresh_icon)
        ta.recycle()
        touchControl.clickListener = {
            if (touchControl.originY > height - bottomDrawableHeight) {
                //**点击的有效区域
                if (isShowFull) hide()
                else expand()
            }
        }
    }

    private fun setText() {
        if (text == null || mWidth == 0) {
            return
        }
        requestMeasure=false
        staticLayout = StaticLayout.Builder.obtain(text!!, 0, text!!.length, paint, mWidth).build()
        fullHeight = staticLayout!!.height + paddingTop + paddingBottom + bottomDrawableHeight
        val mMinLine = min(staticLayout!!.lineCount, minLines)
        hideHeight = if (mMinLine != 0) {
            paddingBottom + paddingTop + staticLayout!!.getLineBottom(mMinLine - 1) + bottomDrawableHeight
        } else {
            paddingBottom + paddingTop + bottomDrawableHeight
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())
        paint.color = textColor
        staticLayout?.draw(canvas)
        canvas.restore()

        arrow.setBounds(width / 2 - bottomDrawableHeight / 2, height - bottomDrawableHeight - paddingBottom, width / 2 + bottomDrawableHeight / 2, height - paddingBottom)
        paint.color = bottomColor
        canvas.drawRect(0f, height.toFloat() - bottomDrawableHeight - paddingBottom, width.toFloat(), height.toFloat(), paint)
        arrow.draw(canvas)

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchControl.onTouchEvent(event)
        return true
    }

    fun hide() {
        if (staticLayout == null) return
        if (isShowFull) {
            if (hideAnimator == null || !hideAnimator!!.isRunning) {
                hideAnimator = ValueAnimator.ofInt(fullHeight, hideHeight)
                hideAnimator!!.duration = 400
                hideAnimator!!.addUpdateListener {
                    layoutParams.height = it.animatedValue as Int
                    layoutParams = layoutParams
                }
                hideAnimator!!.addListener(object : BaseAnimatorListener() {
                    override fun onAnimationEnd(animation: Animator) {
                        isShowFull = false
                    }
                })
                hideAnimator!!.start()
            }
        }
    }

    fun expand() {
        if (staticLayout == null) return
        if (!isShowFull) {
            if (expandAnimator == null || !expandAnimator!!.isRunning) {
                expandAnimator = ValueAnimator.ofInt(height, fullHeight)
                expandAnimator!!.duration = 400
                expandAnimator!!.addUpdateListener {
                    layoutParams.height = it.animatedValue as Int
                    layoutParams = layoutParams
                }
            }
            expandAnimator!!.addListener(object : BaseAnimatorListener() {
                override fun onAnimationEnd(animation: Animator) {
                    isShowFull = true
                }
            })
            expandAnimator!!.start()
        }
    }
    private fun maxWidth():Float{
        if(text==null)return 0f
        val arr=text!!.split("\n")
        var maxWidth=0f
        arr.forEach {
            maxWidth= max(paint.measureText(it),maxWidth)
        }
        return maxWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        var hSize = MeasureSpec.getSize(heightMeasureSpec)
        if (wMode == MeasureSpec.AT_MOST) {
            val needWidth = maxWidth() + paddingStart + paddingEnd
            wSize = min(needWidth.toInt(), wSize)
            mWidth = wSize - paddingStart - paddingEnd
            setText()
        }
        if (staticLayout == null) {
            setText()
        }
        val needHeight = (if (staticLayout != null) staticLayout!!.height else 0) + paddingBottom + paddingTop + bottomDrawableHeight
        if (hMode == MeasureSpec.AT_MOST) {
            hSize = min(needHeight, hSize)
        } else if (hMode == MeasureSpec.UNSPECIFIED) {
            hSize = needHeight
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.EXACTLY))
    }

}