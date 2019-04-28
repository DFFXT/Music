package com.web.moudle.home.video.model

import com.web.moudle.home.video.bean.VideoRecommendBox
import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.SchedulerTransform
import io.reactivex.Observable

class VideoRecommendModel:BaseRetrofit() {
    fun getRecommendVideo():Observable<VideoRecommendBox>{
        return obtainClass(NetApis.HomePage::class.java)
                .getRecommendVideo()
                .compose(SchedulerTransform())
    }
}