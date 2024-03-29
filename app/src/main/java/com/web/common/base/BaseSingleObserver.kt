package com.web.common.base

import androidx.annotation.CallSuper
import com.web.app.MyApplication
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.music.m.R
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.net.UnknownHostException

abstract class BaseSingleObserver<T>: SingleObserver<T> {
    override fun onSubscribe(d: Disposable) {

    }
    open fun error(e: Throwable){
        MToast.showToast(MyApplication.context, ResUtil.getString(R.string.unkownError)+e.message)
    }
    @CallSuper
    override fun onError(e: Throwable) {
        if(e is UnknownHostException){
            MToast.showToast(MyApplication.context, ResUtil.getString(R.string.noInternet))
        }else{
            error(e)
        }
    }
}