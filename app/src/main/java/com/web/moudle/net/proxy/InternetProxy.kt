package com.web.moudle.net.proxy

import androidx.annotation.WorkerThread
import com.web.common.base.log
import com.web.common.constant.AppConfig
import com.web.common.constant.Constant
import com.web.config.Shortcut
import com.web.data.MusicCache
import com.web.moudle.net.proxy.bean.CacheFromTo
import com.web.moudle.setting.cache.CacheActivity
import org.litepal.crud.DataSupport
import java.io.*
import java.lang.Exception
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.log
import kotlin.math.min

object InternetProxy {
    private const val port=2999
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
        return if(CacheActivity.getCacheEnable()
                &&(originUrl.startsWith("http://")||originUrl.startsWith("https://")))
            "http://127.0.0.1:$port/$originUrl"
        else originUrl
    }
    private fun requestMusic(s: Socket){
        pool.execute {
            val request=CalculateRequest()
            val inputStream=s.getInputStream()
            val o=s.getOutputStream()
            request.calculate(inputStream)

            val musicCache=DataSupport.where("musicPath=?",request.pathWithNoParams)
                    .findFirst(MusicCache::class.java)?: MusicCache(
                    request.pathWithNoParams!!,
                    CacheActivity.getCustomerCachePath()+request.getUniqueValidFileName()!!+"_",
                    ""
            )
            val cacheData= MusicCache.calculateCacheRange(musicCache)
            var res:LongArray
            var repeat=true

            //**构建header
            val builder=StringBuilder()
            if(request.requestRangeStart==null){
                builder.append("HTTP/1.1 200 OK\n")
            }else{
                builder.append("HTTP/1.1 206 Partial Content\n")
            }
            builder.append("Content-Type: application/octet-stream\n")
            builder.append("Accept-Ranges: bytes\n")
            builder.append("Connection: keep-alive\n")

            //一个换行隔开
            builder.append("\n")
            o.write(builder.toString().toByteArray())
            //**↑header构建完成

            do {
                res= readFromDisk(cacheData,
                        request.requestRangeStart?.toLong()?:0L,
                        request.responseRangeEnd?.toLong()?:-1L,
                        musicCache.cachePath,
                        o
                )
                if(res[0]!=res[1]){
                    readFromInternet(request.realPath!!,o,res[0],res[1],musicCache)
                }
                if(res[1]==request.responseRangeEnd?.toLong()?:-1L) repeat=false

            }while (repeat)

            o.flush()
            inputStream.close()
            o.close()
            s.close()
        }
    }



    /**
     * 从网络读取数据
     */
    @JvmStatic
    @WorkerThread
    private fun readFromInternet(path:String,out:OutputStream,from:Long,to: Long,musicCache: MusicCache){
        val url=URL(path)
        val connection=url.openConnection()
        val strTo:String = if(to>from) to.toString() else ""
        connection.setRequestProperty("Range","bytes=$from-$strTo")
        connection.connect()

        //**Internet header
        /*val builder=StringBuilder()
        if(from==0L){
            builder.append("HTTP/1.1 200 OK\n")
        }else{
            builder.append("HTTP/1.1 206 Partial Content\n")
        }

        for(i in 0..Int.MAX_VALUE){
            val key= connection.getHeaderFieldKey(i) ?: break
            builder.append(key+": "+connection.getHeaderField(key)+"\n")
        }
        //一个换行隔开
        builder.append("\n")

        out.write(builder.toString().toByteArray())
        log(builder.toString())*/
        if(AppConfig.cacheEnable)
            streamCopyNoCloseWidthCache(connection.getInputStream(),out,from,to,musicCache)
        else {
            try {
                streamCopyNoClose(connection.getInputStream(),out)
            }catch (e:Exception){
                log("传输终止")
            }

        }
    }





    /**
     * 从磁盘读取缓存数据，同步
     * @param cacheData 缓存数据
     * @param from 需要的数据指针
     * @param to 指针结束
     * @param path 缓存数据源
     * @param out 输出流
     * @return 读取完成，还剩下一些数据需要从网络获取，[0] from [1] to
     *
     */
    private fun readFromDisk(cacheData:List<CacheFromTo>,from:Long,to:Long,path:String,out:OutputStream):LongArray{
        val res=LongArray(2)
        res[0]=from
        res[1]=to
        if(!Shortcut.fileExsist(path))return res
        for(i in cacheData.indices){
            if(from>=cacheData[i].from&&from<cacheData[i].to){//**找到合理的缓存
                res[0]= min(cacheData[i].to,to)
                if(i<cacheData.size-1){
                    res[1]=cacheData[i+1].from
                }
                accessStreamCopyNoClose(RandomAccessFile(path,"r"),from,res[0],out)
                break
            }
        }

        return res
    }

    /**
     * 将输入流拷贝到输出流，输入流自动关闭，输出流不关闭
     * @param to to=-1就是读取全部数据
     */
    @JvmStatic
    @WorkerThread
    private fun accessStreamCopyNoClose(inputStream: RandomAccessFile,from:Long,to:Long, outputStream:OutputStream){

        inputStream.use {
            it.seek(from)
            val byte=ByteArray(1024*20)
            var len:Int
            var need:Long=to-from
            if(to==-1L){//**读取全部数据
                need=Long.MAX_VALUE
            }
            while (true){
                len=it.read(byte)
                if(len<0){
                    break
                }
                if(len<=need){
                    need-=len
                    outputStream.write(byte,0,len)
                }else{
                    outputStream.write(byte,0,need.toInt())
                    break
                }

            }
        }
    }

    /**
     * 不带缓存的输出
     * 将输入流拷贝到输出流，输入流自动关闭，输出流不关闭
     */
    @JvmStatic
    @WorkerThread
    private fun streamCopyNoClose(inputStream: InputStream, outputStream:OutputStream){

        BufferedInputStream(inputStream).use {
            val byte=ByteArray(1024*20)
            var len:Int
            while (true){
                len=it.read(byte)
                if(len<0){
                    break
                }
                outputStream.write(byte,0,len)
            }

        }
    }
    /**
     * 带缓存的输出
     * 将输入流拷贝到输出流，输入流自动关闭，输出流不关闭
     */
    @JvmStatic
    @WorkerThread
    private fun streamCopyNoCloseWidthCache(inputStream: InputStream, outputStream:OutputStream,from: Long,to: Long,musicCache: MusicCache){

        BufferedInputStream(inputStream).use {
            val file= File(musicCache.cachePath)
            if(!file.exists()){
                file.createNewFile()
            }
            RandomAccessFile(musicCache.cachePath,"rw").use {access->
                access.seek(from)
                val minBlockToSave=1024*100
                val byte=ByteArray(1024*20)
                var len:Int
                var mTo=from
                var offset=0
                while (true){
                    len=it.read(byte)
                    if(len<0){
                        break
                    }
                    mTo+=len
                    offset+=len
                    outputStream.write(byte,0,len)
                    access.write(byte,0,len)
                    if(offset>=minBlockToSave){
                        offset=0
                        musicCache.addRange(from,mTo)
                    }
                }
                musicCache.addRange(from,mTo)
            }

        }
    }


}