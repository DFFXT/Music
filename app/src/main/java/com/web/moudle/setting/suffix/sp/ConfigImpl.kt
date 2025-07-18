package com.web.moudle.setting.suffix.sp

import android.content.SharedPreferences
import com.fxffxt.preferen.Config
import com.web.moudle.preference.MemorySP

interface ConfigImpl : Config {
    override fun getSharedPreference(): SharedPreferences {
        return try {
            super.getSharedPreference()
        } catch (_: Exception) {
            getMemorySP(localFileName)
        }
    }

    companion object {
        private val map = HashMap<String, MemorySP>()
        fun getMemorySP(name: String): MemorySP {
            if (!map.containsKey(name)) {
                map[name] = MemorySP()
            }
            return map[name]!!
        }
    }
}