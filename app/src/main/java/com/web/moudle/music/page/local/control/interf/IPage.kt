package com.web.moudle.music.page.local.control.interf

import com.web.moudle.music.player.other.IMusicControl

interface IPage {
    fun setConnect(connect: IMusicControl)
    fun getPageName(): String
}