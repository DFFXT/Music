package com.web.moudle.music.player.plugInterface

interface ServiceLifeCycle :Plug{
    abstract fun onCreate()
    abstract fun onDestroy()
}