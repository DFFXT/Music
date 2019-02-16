package com.web.moudle.music.page.recommend

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.web.moudle.billboard.BillBoardActivity
import com.web.moudle.music.page.BaseMusicPage
import com.web.moudle.music.player.MusicPlay
import com.web.web.R

import kotlinx.android.synthetic.main.fragment_recommend.view.*

class RecommendPage :BaseMusicPage(){
    companion object {
        const val pageName="recommendPage"
    }
    override fun setConnect(connect: MusicPlay.Connect) {

    }

    override fun getPageName(): String = pageName

    override fun getLayoutId(): Int = R.layout.fragment_recommend

    override fun setTitle(textView: TextView) {
        textView.text="？？？"
        textView.setCompoundDrawables(null, null, null, null)
    }

    override fun initView(rootView: View) {
        rootView.tv_billboard.setOnClickListener {
            BillBoardActivity.actionStart(context!!)
        }
    }

    override fun viewCreated(view: View, savedInstanceState: Bundle?) {

    }
}