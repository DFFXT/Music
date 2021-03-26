package com.web.common.base

import androidx.annotation.CallSuper
import com.web.app.MyApplication
import com.web.common.tool.MToast
import com.web.common.util.ResUtil
import com.web.web.R
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.net.UnknownHostException

abstract class BaseObserver<T> : Observer<T> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }
    open fun error(e:Throwable){}
    @CallSuper
    override fun onError(e: Throwable) {
        e.printStackTrace()
        if(e is UnknownHostException){
            AndroidSchedulers.mainThread().scheduleDirect{
                MToast.showToast(MyApplication.context,ResUtil.getString(R.string.noInternet))
            }
        }else{
            error(e)
        }
    }
}