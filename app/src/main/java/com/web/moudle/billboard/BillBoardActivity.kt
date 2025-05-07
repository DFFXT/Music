package com.web.moudle.billboard

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.music.m.R
import com.music.m.databinding.ActivityBillboardBinding
import com.web.common.base.BaseViewBindingActivity
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.misc.GapItemDecoration
import com.web.moudle.billboard.adapter.BillboardAdapter
import com.web.moudle.billboard.bean.BillBoardList
import com.web.moudle.billboard.viewmodel.RecommendViewModel

class BillBoardActivity: BaseViewBindingActivity<ActivityBillboardBinding>() {
    private var model:RecommendViewModel?=null
    override fun getLayoutId(): Int =R.layout.activity_billboard

    override fun initView() {
        model=ViewModelProviders.of(this)[RecommendViewModel::class.java]
        model?.billboard?.observe(this,Observer<BillBoardList>{
            if(it==null){
                binding.rootView.showError()
                return@Observer
            }
            //**删除千千音乐U榜，U榜时网页的
            for(i in it.content.indices){
                if(it.content[i].type==500){
                    it.content.removeAt(i)
                    break
                }
            }
            val adapter=BillboardAdapter(it.content)
            binding.rvBillboard.adapter=adapter
            binding.rootView.showContent()
        })



        binding.rvBillboard.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvBillboard.addItemDecoration(GapItemDecoration(left = 10,right = 10,bottom = 10))
        model?.getBillboard()
        binding.rootView.showLoading(true)
    }



    companion object {
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,BillBoardActivity::class.java))
        }
    }
}