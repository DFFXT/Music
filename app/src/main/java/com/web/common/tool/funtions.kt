package com.web.common.tool

import android.util.Log
import com.scwang.smartrefresh.layout.SmartRefreshLayout

fun SmartRefreshLayout.noMore(noMore:Boolean){
    this.setNoMoreData(noMore)
    Log.i("log","----$noMore")
}