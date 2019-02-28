package com.web.moudle.net.proxy

import com.web.common.util.IOUtil
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object InternetProxy {
    private const val port=9999
    private val pool=ThreadPoolExecutor(10,10,1,TimeUnit.SECONDS,LinkedBlockingDeque())

    private var run=true

    fun stopProxy(){
        run=false
    }
    fun startProxy() {
        pool.execute {
            val ss=ServerSocket(port)
            while (run){
                val s=ss.accept()
                requestMusic(s)
            }
        }
    }
    @JvmStatic
    fun proxyUrl(originUrl:String):String{
        return if(originUrl.startsWith("http://")||originUrl.startsWith("https://"))
            "http://127.0.0.1:$port/$originUrl"
        else originUrl
    }
    private fun requestMusic(s: Socket){
        pool.execute {
            val request=CalculateRequest()
            val inputStream=s.getInputStream()
            request.calculate(inputStream)



            val url=URL(request.realPath)
            val connection=url.openConnection()
            if(request.requestRangeStart!=null){
                connection.setRequestProperty("Range","bytes=${request.requestRangeStart}-")
            }
            connection.connect()



            val builder=StringBuilder()
            if(request.requestRangeStart==null){
                builder.append("HTTP/1.1 200 OK\n")
            }else{
                builder.append("HTTP/1.1 206 Partial Content\n")
            }

            for(i in 0..Int.MAX_VALUE){
                val key= connection.getHeaderFieldKey(i) ?: break
                builder.append(key+": "+connection.getHeaderField(key)+"\n")
            }
            //**一个换行隔开
            builder.append("\n")
            val o=s.getOutputStream()
            o.write(builder.toString().toByteArray())
            try {
                IOUtil.streamCopy(connection.inputStream,o)
            }catch (e:Throwable){
                e.printStackTrace()
            }
            o.flush()
            inputStream.close()
            o.close()
            s.close()
        }
    }


}