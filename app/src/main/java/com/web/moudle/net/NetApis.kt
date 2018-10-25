package com.web.moudle.net

import com.web.moudle.net.baseBean.BaseNetBean2
import io.reactivex.Observable
import retrofit2.http.GET
import java.util.*

class NetApis {
    interface Music{
        @GET("http://59.37.96.220/soso/fcgi-bin/client_search_cp?format=json&t=0&inCharset=GB2312&outCharset=utf-8&qqmusic_ver=1302&catZhida=0&p=1&n=10&w=%E7%A5%9E%E8%AF%9D&flag_qc=0&remoteplace=sizer.newclient.song&new_json=1&lossless=0&aggr=1&cr=1&sem=0&force_zonghe=0")
        fun s():Observable<BaseNetBean2<Any>>
    }
}