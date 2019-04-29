package com.web

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.Exception

class XieCheng {
    @Test
    fun test(){
        f("\u70ed\u95e8")
    }

    fun f(x:String?="22"){
        System.out.println(x)
    }

}