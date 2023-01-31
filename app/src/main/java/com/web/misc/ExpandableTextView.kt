package com.web.misc

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.music.m.R
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

    private val bottomDrawableHeight = ViewUtil.dpToPx(25f)
    private var requestMeasure = true

    var textSize: Float = resources.getDimension(R.dimen.textSize_small)
        set(value) {
            field = value
            requestMeasure = true
            requestLayout()
        }
    var textColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    var text: String? = null
        set(value) {
            field = value
            requestMeasure = true
            requestLayout()
        }
    private var arrow: Bitmap
    var minLines = 5
        set(value) {
            field = value
            setHideFullHeight()
            requestLayout()
        }
    var isShowFull = true
        set(value) {
            if (value) {
                expand()
            } else {
                hide()
            }
            field = value
        }
    private var fullHeight = 0
    private var hideHeight = 0
    private val touchControl = MotionEventControl()
    private var isExpand: Boolean = false
    private var expandAnimator: ValueAnimator? = null
    private var hideAnimator: ValueAnimator? = null
    private var duration = 400L

    private var arrowScale = 0f

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView)
        textSize = ta.getFloat(R.styleable.ExpandableTextView_attr_textSize, textSize)
        textColor = ta.getColor(R.styleable.ExpandableTextView_attr_color, textColor)
        text = ta.getString(R.styleable.ExpandableTextView_attr_text)
        isExpand = ta.getBoolean(R.styleable.ExpandableTextView_attr_isExpand, isExpand)
        ta.recycle()
        arrow = ResUtil.getBitmapFromDrawable(ResUtil.getDrawable(R.drawable.icon_back_black))
        arrowScale = bottomDrawableHeight / arrow.width.toFloat()
        touchControl.clickListener = {
            isShowFull = !isShowFull
            if (touchControl.originY > height - bottomDrawableHeight) {
                //**点击的有效图标区域

            }
        }
        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (text != null) {
                    val mDuration = duration
                    duration = 0
                    isShowFull = isExpand  //***默认折叠在这里设置
                    duration = mDuration
                    viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
                return true
            }
        })
        paint.isAntiAlias = true
    }


    private fun setHideFullHeight() {
        fullHeight = staticLayout!!.height + paddingTop + paddingBottom + bottomDrawableHeight
        val mMinLine = min(staticLayout!!.lineCount, minLines)
        hideHeight = if (mMinLine != 0) {
            paddingBottom + paddingTop + staticLayout!!.getLineBottom(mMinLine - 1) + bottomDrawableHeight
        } else {
            paddingBottom + paddingTop + bottomDrawableHeight
        }
    }

    private fun setText() {
        if (text == null || width == 0 || !requestMeasure) {
            return
        }
        requestMeasure = false
        paint.textSize = textSize
        staticLayout = StaticLayout.Builder.obtain(text!!, 0, text!!.length, paint, width - paddingStart - paddingEnd).build()
        setHideFullHeight()
    }

    private var degree = 90f
    private var arrowMatrix = Matrix()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.clipRect(paddingStart, paddingTop, width - paddingEnd, height - bottomDrawableHeight)
        canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())
        paint.textSize = textSize
        paint.color = textColor
        staticLayout?.draw(canvas)
        canvas.restore()
        arrowMatrix.setRotate(degree, arrow.width / 2f, arrow.height / 2f)
        arrowMatrix.postScale(arrowScale, arrowScale)
        arrowMatrix.postTranslate(width / 2f - bottomDrawableHeight / 2f, height.toFloat() - bottomDrawableHeight - paddingBottom)
        canvas.drawBitmap(arrow, arrowMatrix, paint)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        touchControl.onTouchEvent(event)
        return true
    }

    private fun hide() {
        if (staticLayout == null) return
        if (isShowFull) {
            if (hideAnimator == null || !hideAnimator!!.isRunning) {
                hideAnimator = ValueAnimator.ofInt(fullHeight, hideHeight)
                hideAnimator!!.duration = duration
                hideAnimator!!.addUpdateListener {
                    layoutParams.height = it.animatedValue as Int
                    layoutParams = layoutParams
                    degree = -180 * (layoutParams.height - fullHeight.toFloat()) / (hideHeight - fullHeight) + 90
                }
                hideAnimator!!.start()
            }
        }
    }

    private fun expand() {
        if (staticLayout == null) return
        if (!isShowFull) {
            if (expandAnimator == null || !expandAnimator!!.isRunning) {
                expandAnimator = ValueAnimator.ofInt(height, fullHeight)
                expandAnimator!!.duration = duration
                expandAnimator!!.addUpdateListener {
                    layoutParams.height = it.animatedValue as Int
                    layoutParams = layoutParams
                    degree = 180 * (layoutParams.height - hideHeight.toFloat()) / (fullHeight - hideHeight) - 90
                }
            }
            expandAnimator!!.start()
        }
    }

    private fun maxWidth(): Float {
        if (text == null) return 0f
        val arr = text!!.split("\n")
        var maxWidth = 0f
        arr.forEach {
            maxWidth = max(paint.measureText(it), maxWidth)
        }
        return maxWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        var hSize = MeasureSpec.getSize(heightMeasureSpec)
        val needWidth = maxWidth() + paddingStart + paddingEnd
        if (wMode == MeasureSpec.AT_MOST) {
            wSize = min(needWidth.toInt(), wSize)
        }
        setText()
        val needHeight = (if (staticLayout != null) staticLayout!!.height else 0) + paddingBottom + paddingTop + bottomDrawableHeight
        if (hMode == MeasureSpec.AT_MOST) {
            hSize = min(needHeight, hSize)
        } else if (hMode == MeasureSpec.UNSPECIFIED) {
            hSize = needHeight
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(wSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(hSize, MeasureSpec.EXACTLY))
    }

}