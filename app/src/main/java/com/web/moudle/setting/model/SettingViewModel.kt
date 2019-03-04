package com.web.moudle.setting.model

import com.web.common.base.log
import com.web.common.bean.Version
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SettingViewModel {

    fun requestVersion(success:(Version?)->Unit,error:(()->Unit)?=null){
        GlobalScope.launch {
            try {
                val res=BaseRetrofit().obtainClass(NetApis.Global::class.java)
                        .requestVersionInfo().execute()
                log(res)
                launch (Dispatchers.Main) {
                    success(res.body())
                }
            }catch (e:Throwable){
                launch (Dispatchers.Main) {
                    error?.invoke()
                }
            }
        }

    }
}