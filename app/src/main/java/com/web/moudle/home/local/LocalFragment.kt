package com.web.moudle.home.local

import android.view.View
import com.web.common.base.BaseFragment
import com.web.moudle.home.local.model.LocalModel
import com.web.moudle.music.page.local.MusicActivity
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.setting.ui.SettingActivity
import com.web.web.R
import kotlinx.android.synthetic.main.fragment_local.view.*

class LocalFragment:BaseFragment(){
    private val model = LocalModel()
    override fun getLayoutId(): Int = R.layout.fragment_local

    override fun initView(rootView: View) {
        rootView.topBar.setEndImageListener(View.OnClickListener {
            SettingActivity.actionStart(context)
        })

        rootView.layout_localBg.setOnClickListener {
            MusicActivity.actionStart(context)
        }
        model.getMusicNum {
            rootView.tv_musicNum?.text = it.toString()
        }
        model.getPreferNum {
            rootView.tv_preferNum?.text = it.toString()
        }

        model.getDownloadNum {
            rootView.tv_downloadNum.text = it.toString()
        }


        rootView.layout_fastScan.setOnClickListener {
            MusicPlay.scan(context)
        }

    }


}