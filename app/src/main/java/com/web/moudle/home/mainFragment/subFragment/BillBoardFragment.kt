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
import com.music.m.R
import com.music.m.databinding.FragmentBillboardBinding


class BillBoardFragment : BaseFragment<FragmentBillboardBinding>() {
    override var title = ResUtil.getString(R.string.musicBillboard)
    override fun getLayoutId(): Int = R.layout.fragment_billboard

    private lateinit var vm: MainFragmentViewModel

    override fun initView(rootView: View) {
        vm = ViewModelProviders.of(this)[MainFragmentViewModel::class.java]
        vm.billboard.observe(this, Observer {
            if (it == null) {
                binding.rvBillboard.showError()
            } else {
                val adapter = BillboardAdapter(it.content)
                binding.rvBillboard.adapter = adapter
                binding.root.showContent()
            }
        })

        binding.rvBillboard.layoutManager = LinearLayoutManager(context)
        binding.rvBillboard.addItemDecoration(GapItemDecoration(left = 10, right = 10, bottom = 10))
        binding.rvBillboard.showLoading()
        vm.getBillboardList()
    }
}
