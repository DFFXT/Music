package com.web.moudle.billboradDetail.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.web.common.base.get
import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.net.retrofit.SchedulerTransform

class NetMusicListViewModel : ViewModel() {

    private val model = NetMusicListModel()
    val netMusicList = MutableLiveData<NetMusicBox>()

    fun requestList(type: Int) {
        model.requestList(type)
                .compose(SchedulerTransform())
                .get(
                        onNext = {
                            netMusicList.value = it
                        },
                        onError = {
                            netMusicList.value = null
                            it.printStackTrace()
                        }
                )
    }
}