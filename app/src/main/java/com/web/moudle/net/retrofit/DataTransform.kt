package com.web.moudle.net.retrofit

import com.web.moudle.net.baseBean.BaseDataBean
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class DataTransform<T>:ObservableTransformer<BaseDataBean<T>,T> {
    override fun apply(upstream: Observable<BaseDataBean<T>>): ObservableSource<T> {
        return upstream.map { it.data }
    }
}