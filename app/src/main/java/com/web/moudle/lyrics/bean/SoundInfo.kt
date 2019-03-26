package com.web.moudle.lyrics.bean

import org.litepal.crud.DataSupport
import java.io.Serializable

data class SoundInfo(
        val title:String,
        var value:Int,
        val min:Int,
        val max:Int
):DataSupport(),Serializable