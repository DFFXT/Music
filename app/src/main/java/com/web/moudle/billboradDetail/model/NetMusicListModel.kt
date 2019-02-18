package com.web.moudle.billboradDetail.model

import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.billboradDetail.bean.RecommendMusicBox
import com.web.moudle.net.NetApis
import com.web.moudle.net.baseBean.BaseNetBean
import com.web.moudle.net.retrofit.BaseRetrofit
import io.reactivex.Observable

class NetMusicListModel:BaseRetrofit() {

    fun requestList(type:Int):Observable<NetMusicBox>{
        return obtainClass(NetApis.NetMusicList::class.java)
                .requestList(type)
    }
    fun requestRecommend():Observable<BaseNetBean<RecommendMusicBox>>{
        return obtainClass(NetApis.NetMusicList::class.java)
                .requestRecommend()
    }

}