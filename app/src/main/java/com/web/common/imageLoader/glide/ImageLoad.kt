package com.web.common.imageLoader.glide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.web.common.base.MyApplication

object ImageLoad {
    @JvmStatic
    fun load(path:String):GlideRequest<Drawable>{
        return GlideApp.with(MyApplication.context).load(path).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).centerCrop()
    }
    @JvmStatic
    fun loadAsBitmap(path:String):GlideRequest<Bitmap>{
        return GlideApp.with(MyApplication.context).asBitmap().load(path).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(false).centerCrop()
    }
}