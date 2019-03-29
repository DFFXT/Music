package com.web.moudle.videoEntry.model

import com.web.moudle.net.NetApis
import com.web.moudle.net.retrofit.BaseRetrofit
import com.web.moudle.net.retrofit.ResultTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.moudle.videoEntry.bean.VideoInfoBox
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VideoModel:BaseRetrofit() {
    fun getVideoInfo(videoId:String="",songId:String=""):Observable<VideoInfoBox>{
        return obtainClass(NetApis.VideoEntry::class.java)
                .getVideoInfo(videoId,songId)
                .compose(ResultTransform())
                .compose(SchedulerTransform())
    }

    fun getRealPlayAddress(fakeUrl:String,callback:((String?)->Unit)){
        obtainClassNoConverterNoRedirect(NetApis.VideoEntry::class.java)
                .getMvUrl(fakeUrl)
                .enqueue(object : Callback<Any> {
                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        callback.invoke(null)
                    }

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        var flg = false
                        response.raw().header("Location")?.let {
                            callback.invoke(it)
                            flg=true
                        }
                        if (!flg) {
                            callback.invoke(fakeUrl)
                        }
                    }

                })
    }
}