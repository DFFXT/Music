package com.web.moudle.net.baseBean

import com.alibaba.fastjson.annotation.JSONField

class BaseNetBean2<T> {
    @JSONField(name = "code")
    private var code: Int = 0
    @JSONField(name = "data")
    private var data: T? = null

    fun getCode(): Int {
        return code
    }

    fun setCode(code: Int) {
        this.code = code
    }

    fun getData(): T? {
        return data
    }

    fun setData(data: T) {
        this.data = data
    }


}
