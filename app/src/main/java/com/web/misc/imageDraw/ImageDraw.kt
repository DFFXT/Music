package com.web.misc.imageDraw

import android.graphics.Canvas
import android.view.View

interface ImageDraw {
    fun onCreate(){}
    fun draw(v: View, canvas: Canvas)
    fun destroy(){}
}