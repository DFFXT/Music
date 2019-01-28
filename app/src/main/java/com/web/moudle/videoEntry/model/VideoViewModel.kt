package com.web.moudle.videoEntry.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.alibaba.fastjson.JSON
import com.web.common.base.get
import com.web.moudle.videoEntry.bean.FileInfo
import com.web.moudle.videoEntry.bean.RemoteFiles
import com.web.moudle.videoEntry.bean.VideoInfoBox

class VideoViewModel:ViewModel() {
    private val model=VideoModel()
    val videoInfo=MutableLiveData<VideoInfoBox>()
    val videoUrl=MutableLiveData<String>()

    fun getVideoInfo(videoId:String){
        model.getVideoInfo(videoId)
                .get(
                 onNext = {
                     val fileInfo=JSON.parseObject(it.rowJson.getJSONObject(it.min_definition).toJSONString(),FileInfo::class.java)
                     it.fileInfo=fileInfo
                     videoInfo.value=it
                     getVideoUrl(it.fileInfo.source_path)
                 }, onError = {
                            videoInfo.value=null
                        }
                )
    }
    private fun getVideoUrl(fakeUrl:String){
        model.getRealPlayAddress(fakeUrl){
            videoUrl.value=it
        }
    }

}