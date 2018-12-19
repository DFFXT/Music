package com.web.common.bean;

public class LiveDataWrapper<T> {
    private int code;
    private T value;

    public final static int CODE_OK=999;
    public final static int CODE_ERROR=998;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
