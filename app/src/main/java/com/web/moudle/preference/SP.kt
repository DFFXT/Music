package com.web.moudle.preference

import android.app.Activity
import com.web.common.base.MyApplication

object SP {
    fun putValue(name:String,key:String,value:Any){
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        val editor=sp.edit()
        when(value){
            is String->editor.putString(key,value)
            is Int ->editor.putInt(key,value)
            is Boolean ->editor.putBoolean(key,value)
            is Long ->editor.putLong(key,value)
            is Float ->editor.putFloat(key,value)
        }
        editor.apply()
    }
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T:Any> getValue(name: String, key: String, type:Class<Any>):T{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
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
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getInt(key,defValue)
    }
    fun getString(name: String,key: String,defValue: String=""):String{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getString(key,defValue)!!
    }
    fun getBoolean(name: String,key: String,defValue: Boolean=false):Boolean{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getBoolean(key,defValue)
    }
    fun getFloat(name: String,key: String,defValue: Float=0f):Float{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getFloat(key,defValue)
    }
    fun getLong(name: String,key: String,defValue: Long=0):Long{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getLong(key,defValue)
    }
}