package com.web.common.util

import android.support.annotation.WorkerThread
import android.text.TextUtils
import java.io.*
import java.lang.Exception
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
    fun streamCopy(inputStream:InputStream,outputStream:OutputStream,progressCallBack:((Int)->Unit)?=null,stopCallback:((Boolean)->Unit)?=null,stop: StreamStop?=null){
        inputStream.use {input->
            BufferedInputStream(input).use {bis->
                outputStream.use {out->
                    BufferedOutputStream(out).use {bos->
                        var offset=0
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
                            progressCallBack?.invoke(offset)
                        }
                    }
                }
            }

        }
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
        try {
            if(TextUtils.isEmpty(url))return
            val mUrl=URL(url)
            val outputStream=FileOutputStream(savePath)
            streamCopy(mUrl.openStream(),outputStream)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

}