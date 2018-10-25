package com.web.moudle.music.model.control.interf

import com.web.service.MusicPlay

interface IPage {
    fun setConnect(connect:MusicPlay.Connect)
    fun getPageName():String
}