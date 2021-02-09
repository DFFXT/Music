package com.web.misc.imageDraw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import com.web.common.tool.Ticker
import kotlin.math.min
import kotlin.random.Random

class SnowDraw:ImageDraw {
    private lateinit var view: View
    private val snows = ArrayList<Snow>()
    private var width = -1
    private val random = Random(System.currentTimeMillis())
    private val paint = Paint()
    private var additionalX = 0f
    private var additionalY = 0f
    private val ticker = Ticker(10000,0){
        additionalX = (random.nextFloat() -0.5f)*4
        additionalY = (random.nextFloat())*3
    }
    fun init(){
        paint.color = Color.WHITE
        snows.clear()
        for (i in 0..1000){
            snows.add(createSnow(random.nextInt(0,view.measuredHeight).toFloat()))
        }
        ticker.start()
    }
    override fun draw(v: View, canvas: Canvas) {
        this.view = v
        if (width!=v.measuredWidth){
            width = v.measuredWidth
            init()
        }
        snows.forEachIndexed{index,snow->
            //updateSnow(index,it)
            snow.apply {
                if ((x + xSpeed + additionalX).toInt() !in minX.toInt() until maxX.toInt() || (y + ySpeed+additionalY).toInt() !in minY.toInt() until maxY.toInt()){
                    snows[index] = createSnow(1f)
                }
                snow.x += xSpeed + additionalX
                snow.y += ySpeed + additionalY
                alpha = min(1f,(y-minY)/minY + 0.2f)
                paint.alpha = (alpha* 255).toInt()
                canvas.drawCircle(x,y,2f,paint)

            }

        }
        v.postInvalidate()
    }
    private fun updateSnow(index:Int,snow: Snow){

    }
    private fun createSnow(setY:Float):Snow{
        return Snow().apply {
            x = random.nextInt(0,view.measuredWidth).toFloat()
            y = setY
            xSpeed = ((random.nextFloat() -0.5f)*2)
            ySpeed = 1+random.nextFloat() * 5
            minX = 0f
            minY = 0f
            maxX = view.measuredWidth.toFloat()
            maxY = view.measuredHeight.toFloat()
            alpha = min(1f,y/maxY + 0.2f)
        }
    }

    override fun destroy() {
        ticker.stop()
    }
}

class Snow{
    var x = 0f
    var y = 0f
    var xSpeed:Float = 0f
    var ySpeed:Float = 0f
    var minX = 0f
    var maxX = 0f
    var maxY = 0f
    var minY = 0f
    var alpha = 0f
}