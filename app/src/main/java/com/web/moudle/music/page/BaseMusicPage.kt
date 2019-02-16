package com.web.moudle.music.page

import android.widget.TextView
import com.web.common.base.BaseFragment
import com.web.moudle.music.player.MusicPlay

abstract class BaseMusicPage:BaseFragment() {
    abstract fun setConnect(connect: MusicPlay.Connect)
    abstract fun getPageName():String
    abstract fun setTitle(textView:TextView)
}