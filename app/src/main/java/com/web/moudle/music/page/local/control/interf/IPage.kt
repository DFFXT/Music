package com.web.moudle.music.page.local.control.interf

import com.web.moudle.music.player.MusicPlay

interface IPage {
    fun setConnect(connect: MusicPlay.Connect)
    fun getPageName():String
}