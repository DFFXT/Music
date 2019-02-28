package com.web.common.tool

class ThreadLooper {
    var timeGap=500L
    var callback:(()->Void)?=null

    private var run=false

    private var thread:Thread?=null


    fun start(){
        if(run)return
        run=true
        thread=Thread{
            while (run){
                Thread.sleep(timeGap)
                callback?.invoke()
            }
        }
        thread?.start()
    }
    fun stop(){
        run=false
    }

}