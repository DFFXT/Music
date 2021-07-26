package com.web.common.base

interface OnItemClickListener<T> {
    fun itemClick(item: T, position: Int)
}