package com.web.moudle.songSheetEntry.model

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.web.common.base.BaseObserver
import com.web.common.base.BaseSingleObserver
import com.web.common.base.SingleSchedulerTransform
import com.web.common.base.get
import com.web.moudle.songSheetEntry.bean.SongSheetInfo
import io.reactivex.Single
import java.lang.Exception

class SongSheetViewModel:ViewModel() {

    private val model=SongSheetModel()
    val songSheetInfo=MutableLiveData<SongSheetInfo>()


    fun getSongSheetInfo(sheetId:String,page:Int){
        model.getSongSheetInfo(sheetId,page)
                .get(
                        onNext = {
                            songSheetInfo.value = it
                        },
                        onError = {
                            it.printStackTrace()
                            songSheetInfo.value=null
                        }
                )


    }

}