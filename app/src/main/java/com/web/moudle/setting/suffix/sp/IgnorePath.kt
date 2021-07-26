package com.web.moudle.setting.suffix.sp

import com.fxffxt.preferen.Config
import com.fxffxt.preferen.noneNull

/**
 * 忽略的路径
 */
class IgnorePath(override val localFileName: String = "MusicIgnorePath") : Config {
    private var wrapper by noneNull(Wrapper())
    var ignorePathList
        get() = wrapper.ignorePathList
        set(value) {
            val w = wrapper
            w.ignorePathList = value
            // 触发保存
            wrapper = w
        }
    class IgnoreItem(var path: String, var disabled: Boolean)
    private class Wrapper {
        var ignorePathList = mutableListOf<IgnoreItem>()
    }
}