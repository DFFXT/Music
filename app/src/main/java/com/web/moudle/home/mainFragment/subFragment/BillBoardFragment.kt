package com.web.moudle.home.mainFragment.subFragment

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseFragment
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.common.util.ResUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.billboard.adapter.BillboardAdapter
import com.web.moudle.home.mainFragment.model.MainFragmentViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_billboard.view.*

class BillBoardFragment:BaseFragment() {
    override var title =ResUtil.getString(R.string.musicBillboard)
    override fun getLayoutId(): Int= R.layout.fragment_billboard

    private lateinit var vm:MainFragmentViewModel

    override fun initView(rootView: View) {
        vm=ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
        vm.billboard.observe(this, Observer {
            if(it==null){
                rootView.rv_billboard.showError()
            }else{
                val adapter= BillboardAdapter(it.content)
                rootView.rv_billboard.adapter=adapter
                rootView.rootView.showContent()
            }
        })


        rootView.rv_billboard.layoutManager=LinearLayoutManager(context)
        rootView.rv_billboard.addItemDecoration(GapItemDecoration(left = 10,right = 10,bottom = 10))
        rootView.rv_billboard.showLoading()
        vm.getBillboardList()

    }
}