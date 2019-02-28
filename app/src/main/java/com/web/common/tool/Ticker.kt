package com.web.common.tool

import com.web.common.base.log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlin.coroutines.CoroutineContext

/**
 * 每隔一段时间在主线程进行一次回调
 */
class Ticker(private val delay: Long, private val dispatcher: CoroutineDispatcher = Dispatchers.Default, private val callBack: (() -> Unit))  {
    private var ticker: ReceiveChannel<Unit>? = null
    private var run = false
    @ObsoleteCoroutinesApi
    fun start() {
        if (run) return
        run = true
        GlobalScope.launch(Dispatchers.Default) {
            ticker = ticker(delay, 0)
            launch(dispatcher) {
                for (i in ticker!!) {
                    callBack()
                    log("----------${System.currentTimeMillis()}")
                }
            }
        }
    }

    fun stop() {
        //job.cancel()
        ticker?.cancel()
        run = false
    }
}