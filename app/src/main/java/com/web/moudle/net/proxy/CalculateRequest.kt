package com.web.moudle.net.proxy


import java.io.InputStream
import java.net.URLConnection
import java.util.regex.Pattern

class CalculateRequest {
    private var requestHead: String? = null
    private var requestLines: List<String>? = null
    var requestRangeStart: String? = null
    var responseRangeStart: String? = null
    var responseRangeEnd: String? = null
    var responseContentLength: String? = null
    var realPath:String?=null
    var pathWithNoParams:String?=null
    var params=HashMap<String,String>()

    fun calculate(inputStream: InputStream) {
        //**这里只能一次读取，如果进行<0判断会一直阻塞
        requestRangeStart=null
        realPath=null
        pathWithNoParams=null
        var count=inputStream.available()
        if(count==0) count=1024*20
        val by = ByteArray(count)
        val len = inputStream.read(by)
        val head = String(by, 0, len)
        requestLines = head.split("\n")
        getRange()
        getRealPath()
    }

    /**
     * 根据url返回一个合法文件名
     */
    fun getUniqueValidFileName():String?{
        return pathWithNoParams?.replace('/','-')
                ?.replace('\\','-')
                ?.replace(':','-')
    }

    private fun getRange() {

        //val URL_PATTERN = Pattern.compile("GET /(.*) HTTP")
        if (requestLines == null) return
        for (i in 0 until requestLines!!.size) {
            val matcher = RANGE_HEADER_PATTERN.matcher(requestLines!![i])
            if (matcher.find()) {
                requestRangeStart = matcher.group(1)
                return
            }
        }
    }
    private fun getRealPath(){
        if(requestLines==null)return
        params.clear()
        realPath = requestLines!![0].substring(5,requestLines!![0].length-10)
        var index=realPath!!.indexOf('?',0)
        if(index<0)return
        pathWithNoParams=realPath!!.substring(0,index)
        val rowParams=realPath!!.substring(index+1)
        var start=0
        var calculate=true
        while (calculate){
            index=rowParams.indexOf('=',start)
            var key:String
            if(index<=0){
                key=""
            }else{
                key=rowParams.substring(start,index)
            }

            val valueStart=index+1
            var value:String
            if(valueStart>=rowParams.length){
                params[key]=""
                break
            }
            val valueEnd=rowParams.indexOf('&',valueStart)
            if(valueEnd>=rowParams.length||valueEnd<0){
                value=rowParams.substring(valueStart)
                calculate=false
            }else{
                value=rowParams.substring(valueStart,valueEnd)
            }
            start=valueEnd+1
            params[key]=value
        }



    }

    companion object {
        val RANGE_HEADER_PATTERN = Pattern.compile("[R,r]ange:[ ]?bytes=(\\d*)-")
    }

}