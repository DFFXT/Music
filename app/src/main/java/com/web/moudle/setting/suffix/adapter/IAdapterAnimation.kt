package com.web.moudle.setting.suffix.adapter

interface IAdapterAnimation {
    fun remove(position: Int)

    /**
     * position = -1 添加到最后
     */
    fun insert(position: Int)
}