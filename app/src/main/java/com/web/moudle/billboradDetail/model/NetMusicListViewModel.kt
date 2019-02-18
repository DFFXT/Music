package com.web.moudle.billboradDetail.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.web.common.base.get
import com.web.common.util.ResUtil
import com.web.moudle.billboradDetail.bean.BillBoardInfo
import com.web.moudle.billboradDetail.bean.NetMusicBox
import com.web.moudle.net.retrofit.ResultTransform
import com.web.moudle.net.retrofit.SchedulerTransform
import com.web.web.R
import java.text.SimpleDateFormat
import java.util.*

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

    fun requestRecommend() {
        model.requestRecommend()
                .compose(SchedulerTransform())
                .compose(ResultTransform())
                .get(
                        onNext = {
                            val b = BillBoardInfo()
                            b.update_date=SimpleDateFormat("YYYY MM dd", Locale.CHINA).format(Date(it.date))
                            b.billboard_songnum=it.total.toString()
                            b.name=ResUtil.getString(R.string.todayRecommend)
                            b.color=it.color
                            b.bg_color=it.bg_color
                            val box = NetMusicBox(it.list, b)
                            netMusicList.value = box
                        },
                        onError = {
                            netMusicList.value = null
                        }
                )
    }
}