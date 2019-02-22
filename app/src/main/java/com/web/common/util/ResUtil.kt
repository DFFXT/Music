package com.web.common.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.web.common.base.MyApplication
import org.litepal.LitePalApplication
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object ResUtil {
    @JvmStatic
    fun getString(@StringRes id: Int): String {
        return LitePalApplication.getContext().resources.getString(id)
    }
    @JvmStatic
    fun getString(@StringRes id:Int, vararg params:Any):String{
        return String.format(ResUtil.getString(id),*params)
    }

    /**
     * @param pattern mm:ss
     */
    @JvmStatic
    fun timeFormat(pattern: String, time: Long): String {
        val format = SimpleDateFormat(pattern, Locale.CHINA)
        return format.format(Date(time))
    }
    @JvmStatic
    fun getFileSize(size :Long):String{
        val format= DecimalFormat("0.00")
        return when(size){
            in 0..1024*1024-> format.format(size/1024F)+"KB"
            else->format.format(size/1024F/1024)+"MB"
        }
    }
    @JvmStatic
    fun getColor(@ColorRes colorId:Int):Int{
        return MyApplication.context.resources.getColor(colorId,MyApplication.context.theme)
    }
    @JvmStatic
    fun getDrawable(@DrawableRes drawableId: Int):Drawable{
        return MyApplication.context.resources.getDrawable(drawableId,MyApplication.context.theme)
    }
    @JvmStatic
    fun getBitmapFromDrawable(drawable: Drawable):Bitmap{
        val bitmap=Bitmap.createBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,Bitmap.Config.ARGB_4444)
        val canvas=Canvas(bitmap)
        //********必须设置bounds
        drawable.setBounds(0,0,canvas.width,canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
    @JvmStatic
    fun bitmapOp(bitmap:Bitmap,matrix:Matrix):Bitmap{
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,false)
    }
    @JvmStatic
    fun getBitmapHorizontalMirror(bitmap: Bitmap):Bitmap{
        val matrix=Matrix()
        matrix.postScale(-1f,1f)
        return bitmapOp(bitmap,matrix)
    }
    @JvmStatic
    fun getDrawableHorizontalMirror(@DrawableRes drawableId: Int):Bitmap{
        val mBitmap=getBitmapFromDrawable(getDrawable(drawableId))
        val res=getBitmapHorizontalMirror(mBitmap)
        mBitmap.recycle()
        return res
    }
    @JvmStatic
    fun getBitmapRotate(bitmap: Bitmap,degree:Float):Bitmap{
        val matrix=Matrix()
        matrix.postRotate(degree)
        return bitmapOp(bitmap,matrix)
    }
    @JvmStatic
    fun getBitmapRotate(@DrawableRes drawableId: Int, degree:Float=0f):Bitmap{
        val mBitmap= getBitmapFromDrawable(getDrawable(drawableId))
        val res= getBitmapRotate(mBitmap,degree)
        mBitmap.recycle()
        return res
    }
}