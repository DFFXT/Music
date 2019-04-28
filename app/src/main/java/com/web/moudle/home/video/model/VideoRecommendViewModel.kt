package com.web.moudle.home.video.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.get
import com.web.moudle.home.video.bean.VideoRecommendBox

class VideoRecommendViewModel:ViewModel() {
    private val model=VideoRecommendModel()
    val recommendVideo=MutableLiveData<VideoRecommendBox>()

    fun getRecommendVideo(){
        return model.getRecommendVideo()
                .get(
                        onNext = {
                            recommendVideo.value=it
                        },
                        onError = {
                            recommendVideo.value=null
                        }
                )
    }
}