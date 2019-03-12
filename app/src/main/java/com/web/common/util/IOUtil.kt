package com.web.common.util

import android.text.TextUtils
import android.util.Base64

import androidx.annotation.WorkerThread
import com.web.common.constant.Constant
import com.web.moudle.music.player.bean.DiskObject
import kotlinx.coroutines.*
import java.io.*
import java.net.URL
import java.util.*

object IOUtil{
    class StreamStop{
        var stop=false
    }

    /**
     * 复制，将一个流里面的数据复制到另一个流
     * 每隔notifyTimeGap发出一次更新通知
     */
    @JvmStatic
    @WorkerThread
    fun streamCopy(inputStream:InputStream,outputStream:OutputStream,max:Int?=-1,progressCallBack:((progress:Int,length:Int?)->Unit)?=null,notifyTimeGap:Long=-1,stopCallback:((complete:Boolean)->Unit)?=null,stop: StreamStop?=null):Int{
        var offset=0
        var update=false
        var job:Job?=null
        if(notifyTimeGap>0){
            job=GlobalScope.launch(Dispatchers.IO) {
                while (this.isActive){
                    delay(notifyTimeGap)
                    update=true
                }
            }
        }
        inputStream.use {input->
            BufferedInputStream(input).use {bis->
                outputStream.use {out->
                    BufferedOutputStream(out).use {bos->
                        val byte=ByteArray(1024*50)
                        var length:Int
                        while (true){
                            if(stop!=null&&stop.stop){
                                job?.cancel()
                                stopCallback?.invoke(false)
                                break
                            }
                            length=bis.read(byte)
                            if(length<0){
                                job?.cancel()
                                stopCallback?.invoke(true)
                                break
                            }
                            offset+=length
                            bos.write(byte,0,length)
                            bos.flush()
                            if(update){
                                progressCallBack?.invoke(offset,max)
                                update=false
                            }
                        }
                    }
                }
            }

        }
        return offset
    }


    @JvmStatic
    @WorkerThread
    fun streamAccess(inputStream: InputStream,randomAccess: RandomAccessFile,seekTo:Long=0,@WorkerThread progressCallBack:((Int)->Unit)?=null,notifyTimeGap: Long=-1,@WorkerThread stopCallback:((isComplete:Boolean,length:Long)->Unit)?=null,stop: StreamStop?=null){
        var update=false
        var job:Job?=null
        if(notifyTimeGap>0){
            job=GlobalScope.launch {
                while (isActive){
                    delay(notifyTimeGap)
                    update=true
                }
            }
        }

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
                            job?.cancel()
                            stopCallback?.invoke(false,seekTo+offset)
                            break@loop
                        }
                        length=bis.read(byte)
                        if(length<0){//**传输完成
                            job?.cancel()
                            stopCallback?.invoke(true,seekTo+offset)
                            break
                        }
                        offset+=length
                        ra.write(byte,0,length)
                        if(update){
                            update=false
                            progressCallBack?.invoke(offset)//**传输过程
                        }

                    }
                }
            }
        }
    }
    @JvmStatic
    @WorkerThread
    fun onlineDataToLocal(url:String?,savePath:String){
        IOUtil.onlineDataToLocal(url,savePath,null,-1,null,null)
    }
    fun onlineDataToLocal(url:String?,savePath:String,progressCallBack:((progress:Int,length:Int?)->Unit)?=null,notifyTimeGap: Long=-1,stopCallback:((Boolean)->Unit)?=null,stop: StreamStop?=null){
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

            streamCopy(mUrl.openStream(),outputStream,max,progressCallBack,notifyTimeGap,stopCallback = stopCallback,stop = stop)
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

    @JvmStatic
    fun saveObject(diskObject:DiskObject){
        FileOutputStream(diskObject.path).use {fos->
            ObjectOutputStream(fos).use {oos->
                oos.writeObject(diskObject)
            }
        }
    }

    @JvmStatic
    fun objToBase64(diskObject: DiskObject):ByteArray {
        ByteArrayOutputStream().use {byteArray->
            ObjectOutputStream(byteArray).use {
                it.writeObject(diskObject)
            }
            return android.util.Base64.encode(byteArray.toByteArray(), Base64.DEFAULT)
        }
    }
    @Suppress("UNCHECKED_CAST")
    fun <T:DiskObject> base64ToObj(base64String:String?):T?{
        val bytes=Base64.decode(base64String?.toByteArray(),Base64.DEFAULT)
        try {
            ByteArrayInputStream(bytes).use {
                ObjectInputStream(it).use {ois->
                    val obj=ois.readObject()
                    return try {
                        obj as T
                    }catch (e:java.lang.Exception){
                        null
                    }

                }
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        return null
    }
    @JvmStatic
    fun log(str:String){
        val file=File(Constant.LocalConfig.rootPath+"/log.txt")
        FileOutputStream(file,true).use {
            it.write("$str\n\n".toByteArray(Charsets.UTF_8))
        }
    }
}