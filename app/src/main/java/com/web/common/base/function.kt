package com.web.common.base

import io.reactivex.Observable

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