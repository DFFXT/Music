package com.web.common.util

import android.text.TextUtils
import androidx.annotation.WorkerThread
import java.io.*
import java.net.URL

object IOUtil{
    class StreamStop{
        var stop=false
    }

    /**
     * 复制，将一个流里面的数据复制到另一个流
     */
    @JvmStatic
    @WorkerThread
    fun streamCopy(inputStream:InputStream,outputStream:OutputStream,max:Int?=-1,progressCallBack:((progress:Int,length:Int?)->Unit)?=null,stopCallback:((Boolean)->Unit)?=null,stop: StreamStop?=null):Int{
        var offset=0
        inputStream.use {input->
            BufferedInputStream(input).use {bis->
                outputStream.use {out->
                    BufferedOutputStream(out).use {bos->
                        val byte=ByteArray(1024*50)
                        var length:Int
                        while (true){
                            if(stop!=null&&stop.stop){
                                stopCallback?.invoke(false)
                                break
                            }
                            length=bis.read(byte)
                            if(length<0){
                                stopCallback?.invoke(true)
                                break
                            }
                            offset+=length
                            bos.write(byte,0,length)
                            bos.flush()
                            progressCallBack?.invoke(offset,max)
                        }
                    }
                }
            }

        }
        return offset
    }

    @JvmStatic
    @WorkerThread
    fun streamAccess(inputStream: InputStream,randomAccess: RandomAccessFile,seekTo:Long=0,@WorkerThread progressCallBack:((Int)->Unit)?=null,@WorkerThread stopCallback:((Boolean)->Unit)?=null,stop: StreamStop?=null){
        inputStream.use {input->
            BufferedInputStream(input).use {bis->
                randomAccess.use {ra->
                    ra.setLength(seekTo)
                    ra.seek(seekTo)
                    var offset=0
                    val byte=ByteArray(1024*50)
                    var length:Int
                    loop@while (true){
                        if(stop!=null&&stop.stop){//**判断是否暂停
                            stopCallback?.invoke(false)
                            break@loop
                        }
                        length=bis.read(byte)
                        if(length<0){//**传输完成
                            stopCallback?.invoke(true)
                            break
                        }
                        offset+=length
                        ra.write(byte,0,length)
                        progressCallBack?.invoke(offset)//**传输过程
                    }
                }
            }
        }
    }
    @JvmStatic
    @WorkerThread
    fun onlineDataToLocal(url:String?,savePath:String){
        IOUtil.onlineDataToLocal(url,savePath,null,null,null)
    }
    fun onlineDataToLocal(url:String?,savePath:String,progressCallBack:((progress:Int,length:Int?)->Unit)?=null,stopCallback:((Boolean)->Unit)?=null,stop: StreamStop?=null){
        try {
            if(TextUtils.isEmpty(url))return
            val mUrl=URL(url)
            val outputStream=FileOutputStream(savePath)
            val con=mUrl.openConnection()
            val length=con.getHeaderField("Content-Length")
            var max=-1
            try {
                max=length.toInt()
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }

            streamCopy(mUrl.openStream(),outputStream,max,progressCallBack,stopCallback,stop)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    @JvmStatic
    @WorkerThread
    fun streamToBuilder(inputStream: InputStream){
        val builder=StringBuilder()
        inputStream.use {input->
            BufferedInputStream(input).use {bis->
                bis.readBytes()
            }
        }
    }

}