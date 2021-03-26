package com.web.moudle.music.page

import android.os.IBinder
import com.web.common.base.BaseFragment

abstract class BaseMusicPage:BaseFragment() {
    abstract fun setConnect(connect: IBinder)
}