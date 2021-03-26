package com.web.moudle.music.page.local.control.interf

import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.PlayerConnection

interface IPage {
    fun setConnect(connect: PlayerConnection)
    fun getPageName():String
}