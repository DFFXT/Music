package com.web.moudle.music.page

import com.web.common.base.BaseFragment
import com.web.moudle.music.page.local.control.interf.IMusicController
import com.web.moudle.music.player.other.IMusicControl

abstract class BaseMusicPage : BaseFragment() {
    open fun getConnect(): IMusicControl {
        return (activity as IMusicController).getMusicControl()
    }
}