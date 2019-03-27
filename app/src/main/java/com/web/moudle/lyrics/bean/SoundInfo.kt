package com.web.moudle.lyrics.bean

import org.litepal.crud.DataSupport
import java.io.Serializable

/**
 * 在使用时最终的正确值应该是min+value
 */
data class SoundInfo(
        val title:String,
        var value:Int,
        val min:Int,
        val max:Int
):DataSupport(),Serializable