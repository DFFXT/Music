package com.web.common.imageLoader.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.annotation.FloatRange
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.web.app.MyApplication

object ImageLoad {
    @JvmStatic
    fun load(path:String?):RequestBuilder<Drawable>{
        return Glide.with(MyApplication.context).load(path).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false).centerCrop()
    }
    @JvmStatic
    fun loadAsBitmap(path:String?):RequestBuilder<Bitmap>{
        return Glide.with(MyApplication.context).asBitmap().load(path).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false).centerCrop()
    }
    @JvmStatic
    fun buildBlurBitmap(bitmap: Bitmap,@FloatRange(from = 0.0,to = 25.0) radius:Float):Bitmap{
        val out=Bitmap.createBitmap(bitmap.width,bitmap.height,Bitmap.Config.ARGB_8888)
        val rs=RenderScript.create(MyApplication.context)
        val scriptIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val inAllocation=Allocation.createFromBitmap(rs,bitmap)
        val outAllocation=Allocation.createFromBitmap(rs,out)
        scriptIntrinsic.setInput(inAllocation)
        scriptIntrinsic.setRadius(radius)
        scriptIntrinsic.forEach(outAllocation)
        outAllocation.copyTo(out)
        rs.destroy()
        return out
    }
    @JvmStatic
    fun loadBlur(bitmap: Bitmap,@FloatRange(from = 0.0,to = 25.0) radius:Float):RequestBuilder<Drawable>{
        return Glide.with(MyApplication.context).load(buildBlurBitmap(bitmap,radius = radius)).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
    }
}