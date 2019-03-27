package com.web.moudle.setting.model

import com.web.common.bean.Version
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import kotlinx.coroutines.*

object SettingViewModel {
    private var job: Job?=null
    fun requestVersion(success:(Version?)->Unit,error:(()->Unit)?=null){
        job=GlobalScope.launch {
            try {
                if (!isActive){
                    return@launch
                }
                val res= BaseRetrofit().obtainClass(NetApis.Global::class.java)
                        .requestVersionInfo().execute()
                if (!isActive){
                    return@launch
                }
                launch (Dispatchers.Main) {
                    success(res.body())
                }
            }catch (e:Throwable){
                e.printStackTrace()
                launch (Dispatchers.Main) {
                    error?.invoke()
                }
            }
        }
    }
    fun cancelRequest(){
        job?.cancel()
    }
}