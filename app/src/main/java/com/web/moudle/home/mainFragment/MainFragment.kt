package com.web.moudle.home.mainFragment

import android.view.View
import com.web.common.base.BaseFragment
import com.web.moudle.home.local.model.LocalModel
import com.web.web.R

class MainFragment : BaseFragment() {
    private val model = LocalModel()
    override fun getLayoutId(): Int = R.layout.view_textview


    override fun initView(rootView: View) {




    }
}