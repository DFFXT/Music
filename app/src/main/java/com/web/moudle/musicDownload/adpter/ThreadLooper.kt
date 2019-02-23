package com.web.moudle.musicDownload.adpter

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