package com.web.moudle.billboard.model

import com.web.moudle.billboard.bean.BillBoardList
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable

class RecommendModel : BaseRetrofit(){

    fun getBillBroad(): Observable<BillBoardList> {
        return obtainClass(NetApis.HomePage::class.java)
                .requestBillboardList()
                .compose(SchedulerTransform())
    }
}