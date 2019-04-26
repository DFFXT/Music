package com.web

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.Exception

class XieCheng {
    @Test
    fun test(){
        runBlocking {
            val handler = CoroutineExceptionHandler { ctx, exception ->
                println("Caught $ctx")
            }
            val deferred = GlobalScope.async(Dispatchers.IO) {
                throw ArithmeticException() // 没有打印任何东西，依赖用户去调用 deferred.await()
            }
            try {
                deferred.await()
            }catch (e:Exception){
                System.out.println(111)
            }


        }
    }

}