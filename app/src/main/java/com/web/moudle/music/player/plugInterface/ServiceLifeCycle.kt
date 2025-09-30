package com.web.moudle.music.player.plugInterface

interface ServiceLifeCycle :Plug{
    fun onCreate()
    fun onDestroy()
}