package com.web.common.base

import android.support.annotation.CallSuper
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.web.R
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.net.UnknownHostException

open class BaseSingleObserver<T>: SingleObserver<T> {
    override fun onSuccess(t: T) {

    }
    override fun onSubscribe(d: Disposable) {

    }
    open fun error(e: Throwable){

    }
    @CallSuper
    override fun onError(e: Throwable) {
        if(e is UnknownHostException){
            AndroidSchedulers.mainThread().scheduleDirect{
                MToast.showToast(MyApplication.context, ResUtil.getString(R.string.noInternet))
            }
        }else{
            error(e)
        }
    }
}