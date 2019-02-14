package com.web.moudle.net.proxy

import android.util.Log
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

    fun calculate(inputStream: InputStream) {
        //**这里只能一次读取，如果进行<0判断会一直阻塞
        requestRangeStart=null
        realPath=null
        val by = ByteArray(20480)
        val len = inputStream.read(by)
        val head = String(by, 0, len)
        requestLines = head.split("\n")
        Log.i("log", head)
        getRange()
        getRealPath()
    }

    fun calculate(connection: URLConnection) {
        val range = connection.getHeaderField("Content-Range")
        responseContentLength = connection.getHeaderField("Content-Length")
        val RANGE_HEADER_PATTERN = Pattern.compile("[ ]?bytes[ ](\\d*)[ ]?-[ ]?(\\d*)/(\\d*)")
        val matcher = RANGE_HEADER_PATTERN.matcher(range)
        if (matcher.find()) {

        }
    }

    private fun getRange() {
        val RANGE_HEADER_PATTERN = Pattern.compile("[R,r]ange:[ ]?bytes=(\\d*)-")
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
        realPath = requestLines!![0].substring(5,requestLines!![0].length-10)
    }

}