package com.web.moudle.musicSearch.ui

import android.os.Bundle
import android.view.View
import com.web.common.base.BaseFragment

abstract class BaseSearchFragment:BaseFragment() {
    var searchCallBack:((Int)->Unit)?=null
    var keyword:String?=null
    abstract fun search(keyword:String?)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keyword=arguments?.getString(BaseSearchFragment.keyword)
    }

    companion object {
        const val keyword="keyword"
    }
}