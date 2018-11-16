package com.web.moudle.music.page

import com.web.common.base.BaseFragment
import com.web.moudle.music.player.MusicPlay

abstract class BaseMusicPage:BaseFragment() {
    abstract fun setConnect(connect: MusicPlay.Connect)
    abstract fun getPageName():String
}