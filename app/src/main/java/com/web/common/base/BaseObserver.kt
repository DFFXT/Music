package com.web.common.base

import io.reactivex.Observer
import io.reactivex.disposables.Disposable

abstract class BaseObserver<T> : Observer<T> {
    override fun onComplete() {
    }

    override fun onSubscribe(d: Disposable) {
    }

    override fun onError(e: Throwable) {

    }
}