package com.web.misc.imageDraw

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.web.common.base.log
import com.web.common.util.ResUtil
import com.web.web.R
import kotlin.math.pow

/**
 * 绘制柱形图
 */
class WaveDraw:ImageDraw {
    private var paint=Paint()
    private var v:View?=null
    private var dh=-1f
    private var minH=20
    private var gap=0f
    private var jump=8
    var byteArray:ByteArray= ByteArray(2)
        set(value) {
            field= ByteArray(value.size/jump)
            for(i in value.indices step jump){
                var v1= 0.0
                var v2=0.0
                for(di in 0 until jump){
                    if(di<jump/2){
                        v1+=value[i+di]
                    }else{
                        v2+=value[i+di]
                    }

                }
                field[i/jump]= Math.hypot(v1, v2).toByte()
            }
            v?.invalidate()
        }
    override fun draw(v: View, canvas: Canvas) {
        if(dh==-1f){
            dh=(v.height-minH).toFloat().pow(10f/7)/256
            this.v=v
        }
        gap=v.width/byteArray.size.toFloat()
        for(i in byteArray.indices){

            canvas.drawRect(i*gap+1, valToHeight(byteArray[i],v.height,dh = dh),i*gap+gap,v.height.toFloat(),paint)
        }

    }

    private fun valToHeight(v:Byte,height:Int,dh:Float):Float{
        return height- (dh*(v+128f)).pow(0.7f)- minH
    }

    override fun onCreate() {
        paint.color=ResUtil.getColor(R.color.lightBlue)
        paint.alpha=50
    }

}