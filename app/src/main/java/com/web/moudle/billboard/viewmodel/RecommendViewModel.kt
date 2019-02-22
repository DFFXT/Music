package com.web.moudle.billboard.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.web.common.base.get
import com.web.moudle.billboard.bean.BillBoardList
import com.web.moudle.billboard.model.RecommendModel

class RecommendViewModel : ViewModel() {
    private val model = RecommendModel()
    var billboard = MutableLiveData<BillBoardList>()

    fun getBillboard() {
        model.getBillBroad()
                .get(
                        onNext = {
                            billboard.value=it
                        }, onError = {
                            billboard.value=null
                        }
                )
    }

}