package com.web.common.base

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.bumptech.glide.request.transition.Transition
import com.web.common.imageLoader.glide.GlideApp
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ViewUtil
import com.web.web.R
import io.reactivex.Observable
import kotlin.math.max

/**
 * 可以代替 observable.subscribe
 */
fun <T : Any> Observable<T>.get(onNext: (T) -> Unit, onError: ((Throwable) -> Unit)? = null) {
    subscribe(object : BaseObserver<T>() {
        override fun onNext(t: T) {
            onNext.invoke(t)
        }

        override fun error(e: Throwable) {
            onError?.invoke(e)
        }
    })
}


//<editor-fold desc="从网络加载图片到bitmapImageView，并将计算后的颜色给bitmapColorView作为背景">
fun bitmapColorSet(path:String?,bitmapImageView:ImageView,bitmapColorView:View){
    ImageLoad.loadAsBitmap(path).into(object : BaseGlideTarget() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            androidx.palette.graphics.Palette.from(resource).generate {
                it?.vibrantSwatch?.let { sw ->
                    bitmapColorView.setBackgroundColor(sw.rgb)
                }
            }
            bitmapImageView.setImageBitmap(resource)
        }
    })
}
//</editor-fold>


//<editor-fold desc="给seekBar添加seek触发">
fun SeekBar.onSeekTo(onSeekTo:((Int)->Unit)?=null,onChange:(((Int)->Unit))?=null){
    setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(fromUser)
                onChange?.invoke(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            onSeekTo?.invoke(seekBar.progress)
        }
    })
}
//</editor-fold>




//<editor-fold desc="View 的 loading功能">
/**
 * 设置RecyclerView的ItemDecoration ，顺便清除以前的
 */
fun androidx.recyclerview.widget.RecyclerView.setItemDecoration(itemDecoration: androidx.recyclerview.widget.RecyclerView.ItemDecoration){
    for(i in 0 until itemDecorationCount){
        removeItemDecorationAt(0)
    }
    addItemDecoration(itemDecoration)
}



//**loading动画中的点击事件
var View.loadingClickListen: View.OnClickListener?
    get() {
        val listener=getTag(R.integer.loadingListenerId)
        return if(listener==null) null else listener as View.OnClickListener
    }
    set(v) {
        this.setTag(R.integer.loadingListenerId, v)
    }
//**loadingView
var View.loadingView: View?
    get() {
        val v = getTag(R.integer.loadingViewId)
        return if (v == null) null else v as View
    }
    set(v) {
        this.setTag(R.integer.loadingViewId, v)
    }
//**errorView
var View.errorView: View?
    get() {
        val v = getTag(R.integer.errorViewId)
        return if (v == null) null else v as View
    }
    set(v) {
        this.setTag(R.integer.errorViewId, v)
    }

var View.errorClickLinsten:View.OnClickListener?
    get() {
        val listener=getTag(R.integer.errorListenerId)
        return if(listener==null) null else listener as View.OnClickListener
    }
    set(v) {
        this.setTag(R.integer.errorListenerId, v)
    }
/**
 * 对一个view显示Loading动画，采用覆盖方式
 */
fun View.showLoading(isRootLoading: Boolean = false) {
    when {
        width > 0 -> mShowLoading()
        isRootLoading -> {
            mShowLoading(ViewUtil.screenWidth(), ViewUtil.screenHeight())
        }
        else -> {
            if(width!=ViewGroup.LayoutParams.WRAP_CONTENT&&
                    width!=ViewGroup.LayoutParams.MATCH_PARENT&&
                    height!=ViewGroup.LayoutParams.WRAP_CONTENT&&
                    height!=ViewGroup.LayoutParams.MATCH_PARENT){
                mShowLoading()
            }else{
                post {
                    mShowLoading()
                }
            }

        }
    }
}



private fun View.mShowLoading(w: Int = 0, h: Int = 0) {
    hideError()
    var mLoadingView = loadingView
    if (mLoadingView == null) {
        mLoadingView = LayoutInflater.from(context).inflate(R.layout.layout_loading, null, false)
        GlideApp.with(this).asGif().load(R.drawable.loading_gif).into((mLoadingView as ViewGroup).findViewById(R.id.iv_loadingGif))
        loadingView = mLoadingView
    }
    val p = parent as ViewGroup
    p.removeView(mLoadingView)//**防止连续调用addView
    p.addView(mLoadingView, max(width, w), max(height, h))
    mLoadingView.y = y - p.paddingTop
    mLoadingView.x = x - p.paddingStart
    mLoadingView.setOnClickListener { v ->
        loadingClickListen?.let {
            (it).onClick(v)
        }
    }
}


/**
 * 错误界面
 */
fun View.showError(tip:String?=null,drawable:Drawable?=null):ViewGroup {
    hideLoading()
    var mErrorView = errorView
    if (mErrorView == null) {
        mErrorView = LayoutInflater.from(context).inflate(R.layout.layout_error, null, false)
        //GlideApp.with(this).asGif().load(R.drawable.loading_gif).into((mErrorView as ViewGroup).findViewById(R.itemId.iv_refresh))
        errorView = mErrorView
        mErrorView as ViewGroup
    }
    if(tip!=null)
        mErrorView.findViewById<TextView>(R.id.tv_errorTip).text=tip
    if(drawable!=null)
        mErrorView.findViewById<ImageView>(R.id.iv_refresh).setImageDrawable(drawable)
    val p = parent as ViewGroup
    p.addView(mErrorView, width, height)
    mErrorView.y = y - p.paddingTop
    mErrorView.x = x - p.paddingStart
    mErrorView.setOnClickListener { v ->
        errorClickLinsten?.let {
            (it).onClick(v)
        }
    }
    return mErrorView as ViewGroup
}

/**
 * 隐藏loadingView动画
 */
fun View.hideLoading() {
    val mLoadingView = loadingView
    if (mLoadingView != null) {
        post{
            (parent as ViewGroup).removeView(mLoadingView)
        }

    }
}
fun View.hideError(){
    val mErrorView = errorView
    if(mErrorView!=null){
        post {
            (parent as ViewGroup).removeView(mErrorView)
        }
    }
}

/**
 * 还原内容界面
 */
fun View.showContent(){
    hideLoading()
    hideError()
}
//</editor-fold>


/**
 * 使用方法：if(str?.isStrictEmpty()!=false){//str为空....}
 */
fun String.isStrictEmpty():Boolean{
    val string=toString()
    for (i in 0 until string.length) {
        if (string[i] != ' ') return false
    }
    return true
}