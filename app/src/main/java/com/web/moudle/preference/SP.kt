package com.web.moudle.preference

import com.tencent.mmkv.MMKV
import com.web.app.MyApplication
import com.web.common.util.IOUtil
import com.web.moudle.music.player.bean.DiskObject

/**
 * key-value 数据存储
 * 采用mmkv
 */
object SP {

    private val kvMap=HashMap<String,MMKV>()
    init {
        MMKV.initialize(MyApplication.context)
    }
    fun getKV(name: String):MMKV{
        if(kvMap[name]==null) {
            kvMap[name]=MMKV.mmkvWithID(name)
        }
        return kvMap[name]!!
    }
    fun putValue(name:String,key:String,value:Any){
        val kv= getKV(name)
        kv.lock()
        when(value){
            is String-> kv.putString(key,value)
            is Int ->kv.putInt(key,value)
            is Boolean ->kv.putBoolean(key,value)
            is Long ->kv.putLong(key,value)
            is Float ->kv.putFloat(key,value)
            is DiskObject->{
                kv.putString(key, String(IOUtil.objToBase64(value)))
            }
        }
        kv.unlock()
    }

    /**
     * reified 具体化
     * 必须inline修饰，修饰泛型T，
     * 在java中 不能用T判定T的类型
     * 在kt中可以判定T的类型
     */
    inline fun <reified T:Any> getValue(name: String, key: String):T{
        val sp= getKV(name)
        return when(T::class.java.name){
            String::class.java.name->sp.getString(key,"") as T
            Int::class.java.name ->sp.getInt(key,0) as T
            Boolean::class.java.name ->sp.getBoolean(key,false) as T
            Long::class.java.name ->sp.getLong(key,0) as T
            Float::class.java.name ->sp.getFloat(key,0f) as T
            else-> Any() as T
        }
    }
    fun getInt(name: String,key: String,defValue:Int=0):Int{
        val sp=getKV(name)
        return sp.getInt(key,defValue)
    }
    fun getString(name: String,key: String,defValue: String?=null):String{
        val sp=getKV(name)
        return sp.getString(key,defValue)!!
    }
    fun getBoolean(name: String,key: String,defValue: Boolean=false):Boolean{
        val sp=getKV(name)
        return sp.getBoolean(key,defValue)
    }
    fun getFloat(name: String,key: String,defValue: Float=0f):Float{
        val sp=getKV(name)
        return sp.getFloat(key,defValue)
    }
    fun getLong(name: String,key: String,defValue: Long=0):Long{
        val sp=getKV(name)
        return sp.getLong(key,defValue)
    }
    fun <T:DiskObject> getDiskObject(name: String,key: String):T?{
        val sp=getKV(name)
        return IOUtil.base64ToObj(sp.getString(key,""))
    }
}