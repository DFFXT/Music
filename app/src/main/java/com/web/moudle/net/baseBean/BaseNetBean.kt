package com.web.moudle.net.baseBean

data class BaseNetBean<T>(var status:Int?,var error:String?,var errcode:Int?, var data:T)
