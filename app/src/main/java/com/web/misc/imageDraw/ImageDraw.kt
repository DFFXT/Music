package com.web.misc.imageDraw

import android.graphics.Canvas
import android.view.View

interface ImageDraw {
    fun onCreate(v: View){}
    fun draw(v: View, canvas: Canvas)
    fun destroy(){}
}