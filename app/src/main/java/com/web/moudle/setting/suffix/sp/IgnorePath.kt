package com.web.moudle.setting.suffix.sp

import com.fxffxt.preferen.noneNull

/**
 * 忽略的路径
 */
class IgnorePath(override val localFileName: String = "MusicIgnorePath") : ConfigImpl {
    private var wrapper by noneNull(Wrapper())
    var ignorePathList
        get() = wrapper.ignorePathList
        set(value) {
            val w = wrapper
            w.ignorePathList = value
            // 触发保存
            wrapper = w
        }

    fun save() {
        wrapper = wrapper
    }
    class IgnoreItem(var path: String, var enable: Boolean)
    private class Wrapper {
        var ignorePathList = mutableListOf<IgnoreItem>()
    }
}