package com.web.common.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.web.common.imageLoader.glide.GlideApp
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.util.ViewUtil
import com.web.common.util.WindowUtil
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
        else -> post {
            mShowLoading()
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
fun View.showError() {
    hideLoading()
    var mErrorView = errorView
    if (mErrorView == null) {
        mErrorView = LayoutInflater.from(context).inflate(R.layout.layout_error, null, false)
        //GlideApp.with(this).asGif().load(R.drawable.loading_gif).into((mErrorView as ViewGroup).findViewById(R.id.iv_refresh))
        errorView = mErrorView
        mErrorView as ViewGroup
    }
    val p = parent as ViewGroup
    p.addView(mErrorView, width, height)
    mErrorView.y = y - p.paddingTop
    mErrorView.x = x - p.paddingStart
    mErrorView.setOnClickListener { v ->
        loadingClickListen?.let {
            (it).onClick(v)
        }
    }
}

/**
 * 隐藏loadingView动画
 */
fun View.hideLoading() {
    val mLoadingView = loadingView
    if (mLoadingView != null) {
        (parent as ViewGroup).removeView(mLoadingView)
    }
}
fun View.hideError(){
    val mErrorView = errorView
    if(mErrorView!=null){
        (parent as ViewGroup).removeView(mErrorView)
    }
}

/**
 * 还原内容界面
 */
fun View.showContent(){
    hideLoading()
    hideError()
}