package com.web.common.base

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

open class BaseGlideTarget(private val w:Int=Target.SIZE_ORIGINAL, private val h:Int=Target.SIZE_ORIGINAL): Target<Drawable> {
    override fun onLoadStarted(placeholder: Drawable?) {

    }

    override fun onLoadFailed(errorDrawable: Drawable?) {

    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(w,h)
    }

    override fun getRequest(): Request? {
        return null
    }

    override fun onStop() {

    }

    override fun setRequest(request: Request?) {

    }

    override fun removeCallback(cb: SizeReadyCallback) {

    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }

    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {

    }

    override fun onStart() {

    }

    override fun onDestroy() {

    }
}