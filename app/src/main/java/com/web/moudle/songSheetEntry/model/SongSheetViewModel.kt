package com.web.moudle.songSheetEntry.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.get
import com.web.moudle.net.baseBean.BaseNetBean
import com.web.moudle.songSheetEntry.bean.SongSheetInfoBox

class SongSheetViewModel:ViewModel() {

    private val model=SongSheetModel()
    val songSheetInfo=MutableLiveData<BaseNetBean<SongSheetInfoBox>>()


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