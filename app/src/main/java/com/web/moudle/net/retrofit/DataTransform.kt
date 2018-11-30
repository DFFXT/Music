package com.web.moudle.net.retrofit

import com.web.moudle.net.baseBean.BaseNetBean
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class DataTransform<T>:ObservableTransformer<BaseNetBean<T>,T> {
    override fun apply(upstream: Observable<BaseNetBean<T>>): ObservableSource<T> {
        return upstream.map { it.data }
    }
}