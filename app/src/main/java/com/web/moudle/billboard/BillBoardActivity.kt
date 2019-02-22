package com.web.moudle.billboard

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.web.common.base.BaseActivity
import com.web.common.base.showContent
import com.web.common.base.showError
import com.web.common.base.showLoading
import com.web.misc.GapItemDecoration
import com.web.moudle.billboard.adapter.BillboardAdapter
import com.web.moudle.billboard.bean.BillBoardList
import com.web.moudle.billboard.viewmodel.RecommendViewModel
import com.web.web.R
import kotlinx.android.synthetic.main.activity_billboard.*

class BillBoardActivity:BaseActivity() {
    private var model:RecommendViewModel?=null
    override fun getLayoutId(): Int =R.layout.activity_billboard

    override fun initView() {
        model=ViewModelProviders.of(this)[RecommendViewModel::class.java]
        model?.billboard?.observe(this,Observer<BillBoardList>{
            if(it==null){
                rootView.showError()
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
            rv_billboard.adapter=adapter
            rootView.showContent()
        })



        rv_billboard.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        rv_billboard.addItemDecoration(GapItemDecoration(left = 10,right = 10,bottom = 10))
        model?.getBillboard()
        rootView.showLoading(true)
    }



    companion object {
        @JvmStatic
        fun actionStart(ctx:Context){
            ctx.startActivity(Intent(ctx,BillBoardActivity::class.java))
        }
    }
}