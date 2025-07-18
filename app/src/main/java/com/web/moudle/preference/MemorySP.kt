package com.web.moudle.preference

import android.content.SharedPreferences
import java.util.concurrent.ConcurrentHashMap

class MemorySP : SharedPreferences {
    private val map = ConcurrentHashMap<String, Any?>()

    // private val dispatcher = SharedPreferencesListenerDispatcher()

    override fun getAll(): Map<String, *>? {
        return map
    }

    override fun getString(key: String, defValue: String?): String? {
        return map.get(key)?.toString() ?: defValue
    }

    override fun getStringSet(
        key: String,
        defValues: Set<String?>?
    ): Set<String?>? {
        return (map.get(key) as? Set<String?>) ?: defValues
    }

    override fun getInt(key: String?, defValue: Int): Int {
        return map[key] as? Int ?: defValue
    }

    override fun getLong(key: String?, defValue: Long): Long {
        return map[key] as? Long ?: defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        return map[key] as? Float ?: defValue
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return map[key] as? Boolean ?: defValue
    }

    override fun contains(key: String?): Boolean {
        return map.containsKey(key)
    }

    override fun edit(): SharedPreferences.Editor? {
        return editor
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("Not yet implemented")
    }


    private val editor = object : SharedPreferences.Editor {
        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putStringSet(
            key: String,
            values: Set<String?>?
        ): SharedPreferences.Editor {
            map[key] = values
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            map[key] = value
            return this
        }

        override fun putLong(
            key: String,
            value: Long
        ): SharedPreferences.Editor? {
            map[key] = value
            return this
        }

        override fun putFloat(
            key: String,
            value: Float
        ): SharedPreferences.Editor? {
            map[key] = value
            return this
        }

        override fun putBoolean(
            key: String,
            value: Boolean
        ): SharedPreferences.Editor? {
            map[key] = value
            return this
        }

        override fun remove(key: String): SharedPreferences.Editor? {
            map.remove(key)
            return this
        }

        override fun clear(): SharedPreferences.Editor? {
            map.clear()
            return this
        }

        override fun commit(): Boolean {
            return true
        }

        override fun apply() {
        }
    }
}