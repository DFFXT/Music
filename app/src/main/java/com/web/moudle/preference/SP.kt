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
    fun getValue(name: String,key: String,type:Class<Any>):Any{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return when(type.name){
            String::class.java.name->sp.getString(key,"")
            Int::class.java.name ->sp.getInt(key,0)
            Boolean::class.java.name ->sp.getBoolean(key,false)
            Long::class.java.name ->sp.getLong(key,0)
            Float::class.java.name ->sp.getFloat(key,0f)
            else->""
        }
    }
    fun getInt(name: String,key: String):Int{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getInt(key,0)
    }
    fun getString(name: String,key: String):String{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getString(key,"")
    }
    fun getBoolean(name: String,key: String):Boolean{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getBoolean(key,false)
    }
    fun getFloat(name: String,key: String):Float{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getFloat(key,0f)
    }
    fun getLong(name: String,key: String):Long{
        val sp=MyApplication.context.getSharedPreferences(name,Activity.MODE_PRIVATE)
        return sp.getLong(key,0)
    }
}