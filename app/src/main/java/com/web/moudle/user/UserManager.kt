package com.web.moudle.user

import com.web.common.constant.Constant
import com.web.moudle.login.bean.RegistResponse
import com.web.moudle.preference.SP

object UserManager {

    @JvmStatic
    fun login(user:RegistResponse){
        setLogin(true)
        setUserId(user.id.toString())
        setUserName(user.nickname)
    }

    @JvmStatic
    fun setLogin(login:Boolean){
        SP.putValue(Constant.spName, LOGIN,login)
    }


    @JvmStatic
    fun isLogin():Boolean{
        return SP.getBoolean(Constant.spName, LOGIN,false)
    }

    @JvmStatic
    fun getUserName():String{
        return SP.getString(Constant.spName, USERNAME,"---")
    }
    @JvmStatic
    fun setUserName(userName:String){
        return SP.putValue(Constant.spName, USERNAME,userName)
    }

    @JvmStatic
    fun getUserId():String{
        return SP.getString(Constant.spName, USERID,"")
    }
    @JvmStatic
    fun setUserId(userId:String){
        return SP.putValue(Constant.spName, USERID,userId)
    }

    private const val LOGIN="UserManager_login"
    private const val USERNAME="UserManager_userName"
    private const val USERID="UserManager_userId"

}