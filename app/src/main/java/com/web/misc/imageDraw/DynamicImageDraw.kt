package com.web.misc.imageDraw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import com.web.common.base.log
import com.web.common.tool.Ticker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlin.random.Random

class DynamicImageDraw:ImageDraw {
    private val dots= arrayOfNulls<Dot>(60)
    private val paint=Paint()
    private var iv:View?=null
    private var createNum=10
    private val random= Random(System.currentTimeMillis())
    private val boom= arrayOfNulls<Boom>(6)
    private val maxDx=3
    private val ticker=Ticker(50L,100L,Dispatchers.Main){
        if(iv!=null){
            for(i in boom.indices){
                if(boom[i]==null){
                    boom[i]=Boom(iv!!.width.toFloat(),iv!!.height.toFloat(),paint)
                    boom[i]!!.x=random.nextInt(0,iv!!.width).toFloat()
                    boom[i]!!.y=random.nextInt(0,iv!!.height).toFloat()
                    boom[i]!!.ay=random.nextInt(-10,11).toFloat()
                    boom[i]!!.ax=random.nextInt(-4,5).toFloat()
                }else{
                    break
                }
            }
        }
        iv?.postInvalidate()
    }
    @ObsoleteCoroutinesApi
    override fun onCreate() {
        ticker.start()
    }

    override fun draw(v: View, canvas: Canvas) {
        iv=v
        boom.forEach {
            it?.draw(canvas)
        }
    }

    override fun destroy() {
        ticker.stop()
    }

    private class Dot(private val maxX:Float,private val maxY:Float){
        var x:Float=0f
            set(value) {
                if(value>maxX){
                    field=0f
                    //if(dx>0) dx=-dx
                }else if(value<0) {
                    field=maxX
                    //if(dx<0) dx=-dx
                }else{
                    field=value
                }
            }
        var y:Float=0f
            set(value) {
                if(value>=maxY){
                    field=0f
                    //if(dy>0) dy=-dy
                }else if(value<=0) {
                    field=maxY
                    //if(dy<0) dy=-dy
                }else{
                    field=value
                }
            }

        var radius:Float=5f
        var color:Int= Color.RED
        var alpha:Int=128
            set(value){
                if(value<0) field=0
                else if(value>255) field=255
                else field=value
            }
        var dx:Float=0f
        var dy:Float=0f
        var ax:Float=1f
        var ay:Float=1f

        fun tick(){
            x+=dx
            x+=dy
            dx-=ax
            dy-=ay
        }

        fun draw(canvas: Canvas){

        }
    }

    private class Boom(private val maxX:Float,private val maxY:Float,private val paint: Paint){
        var x:Float=20f
            set(value) {
                when {
                    value<=0 -> {
                        field=0f
                        ax=-ax
                    }
                    value>=maxX -> {
                        field=maxX
                        ax=-ax
                    }
                    else -> field=value
                }
            }
        var y:Float=10f
            set(value) {
                when {
                    value<=0 -> {
                        field=0f
                        ay=-ay
                    }
                    value>=maxY -> {
                        field=maxY
                        ay=-ay
                    }
                    else -> field=value
                }
            }
        var radius=4f
        var g=2f
        var ax=8f
        var ay=0f

        var color=Color.RED

        private var boomX=0f
        private var boomY=0f
        fun boom(bx:Float,by:Float){
            val dis=Math.sqrt((bx-x)*(bx-x)+(by-y)*(by-y).toDouble())
            val power=1/dis
            ax= (power*(bx-x)/dis).toFloat()
            ay= (power*(by-y)/dis).toFloat()
        }

        private fun tick(){
            x+=ax
            val preay=ay
            ay+=g
            y+=preay


        }
        fun draw(canvas: Canvas){
            tick()
            paint.color=color
            canvas.drawCircle(x,y,radius,paint)
        }
    }
}