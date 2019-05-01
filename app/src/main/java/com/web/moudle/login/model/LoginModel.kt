package com.web.moudle.login.model

import com.web.moudle.login.bean.RegistResponse
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable

class LoginModel:BaseRetrofit() {

    fun login(account:Long,password:String):Observable<RegistResponse>{
        return obtainClass(NetApis.Login::class.java)
                .login(account,password)
                .compose(SchedulerTransform())
    }

    fun register(account: String,password: String,nickName:String):Observable<RegistResponse>{
        return obtainClass(NetApis.Login::class.java)
                .register(account,password,nickName)
                .compose(SchedulerTransform())
    }
}