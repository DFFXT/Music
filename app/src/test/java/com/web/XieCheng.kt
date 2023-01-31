package com.web

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Test

class XieCheng {
    @Test
    fun test() {
        val f = Gson().fromJson<List<String> >("[\"dd\"]", object : TypeToken<List<String>>() {}.type)
    }
}