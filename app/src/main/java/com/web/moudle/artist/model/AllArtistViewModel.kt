package com.web.moudle.artist.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.get
import com.web.common.bean.LiveDataWrapper
import com.web.common.util.ResUtil
import com.web.moudle.artist.bean.ArtistBox
import com.web.moudle.artist.bean.ArtistType
import com.web.moudle.net.retrofit.SchedulerTransform
import com.music.m.R

class AllArtistViewModel:ViewModel() {
    private val model=ArtistModel()
    private val wrapper=LiveDataWrapper<ArtistBox>()
    val artistList=MutableLiveData<LiveDataWrapper<ArtistBox>>()

    fun getHotArtist(areaCode:Int,sexCode:Int,offset:Int,limit:Int){
        model.getArtist(areaCode,sexCode,offset,limit)
                .compose(SchedulerTransform())
                .get(onNext = {
                    if(it.havemore==1){
                        wrapper.code=LiveDataWrapper.CODE_OK
                    }else{
                        wrapper.code=LiveDataWrapper.CODE_NO_MORE
                    }
                    wrapper.value=it
                    artistList.value=wrapper
                },onError = {
                    wrapper.code=LiveDataWrapper.CODE_ERROR
                    artistList.value=wrapper
                })
    }

    fun getArtistTypeList():List<ArtistType>{
        val names=ResUtil.getStringArray(R.array.artistType_name)
        val areaCode=ResUtil.getIntArray(R.array.artistType_areaCode)
        val sexCode=ResUtil.getIntArray(R.array.artistType_sexCode)
        val list=ArrayList<ArtistType>()
        for(i in names.indices){
            list.add(ArtistType(names[i],areaCode[i],sexCode[i]))
        }
        return list
    }

}