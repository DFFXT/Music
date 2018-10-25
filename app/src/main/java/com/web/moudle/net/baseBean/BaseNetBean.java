package com.web.moudle.net.baseBean;

import com.alibaba.fastjson.annotation.JSONField;
import com.web.web.Text;

public class BaseNetBean<T> {
    @JSONField(name = "code")
    private int code;
    @JSONField(name = "data")
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
