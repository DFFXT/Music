package com.web.common.tool

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker

/**
 * 每隔一段时间在主线程进行一次回调
 */
class Ticker(private val delay: Long,private val initialDelay:Long=0, private val dispatcher: CoroutineDispatcher = Dispatchers.Default, private val callBack: (() -> Unit))  {
    private var ticker: ReceiveChannel<Unit>? = null
    @ObsoleteCoroutinesApi
    fun start() {
        if (ticker!=null) return
        GlobalScope.launch(Dispatchers.Default) {
            ticker = ticker(delay, initialDelay)
            launch(dispatcher) {
                for (i in ticker!!) {
                    callBack()
                }
            }
        }
    }

    fun stop() {
        //job.cancel()
        ticker?.cancel()
        ticker=null
    }
}