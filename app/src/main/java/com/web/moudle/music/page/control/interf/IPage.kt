package com.web.moudle.music.page.control.interf

import com.web.moudle.music.player.MusicPlay

interface IPage {
    fun setConnect(connect: MusicPlay.Connect)
    fun getPageName():String
}