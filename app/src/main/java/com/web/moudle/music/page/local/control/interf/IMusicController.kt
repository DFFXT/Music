package com.web.moudle.music.page.local.control.interf

import com.web.moudle.music.player.other.IMusicControl

/**
 * @author feiqin
 * @date 2021/11/8-9:17
 * @description 音乐控制器
 */
interface IMusicController {
    /**
     * 获取播放器接口
     */
    fun getMusicControl(): IMusicControl
}